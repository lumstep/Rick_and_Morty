package com.lumstep.rickandmorty.location.location_list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.lumstep.rickandmorty.*
import com.lumstep.rickandmorty.location.Location
import com.lumstep.rickandmorty.location.LocationEntity
import com.lumstep.rickandmorty.location.LocationService
import com.lumstep.rickandmorty.pages_data.PagesEntity

import java.io.IOException

class LocationListRepository(
    private val app_database: AppDatabase,
    private val locationApi: LocationService,
    private val locationMapper: Mapper<Location, LocationEntity>
) : ListRepository<Location> {

    override var hasNextPage = MutableLiveData<Boolean>()

    override suspend fun getList(currentPage: Int, filters: HashMap<String, String>): List<Location> {

        try {
            val response =
                if (filters.isEmpty()) locationApi.getLocationPage(currentPage)
                else {
                    locationApi.getLocationFilteredList(
                        currentPage,
                        name = filters["name"],
                        type = filters["type"],
                        dimension = filters["dimension"]
                    )
                }

            if (response.isSuccessful) {
                app_database.getMaxPagesDao().insertMaxPages(PagesEntity("location", response.body()!!.info.pages))

                hasNextPage.value = response.body()?.info?.next != null

                if (filters.isEmpty()) Log.d(
                    "MAIN",
                    "Repository get locationList for page-$currentPage from network : since ${response.body()?.results?.first()?.id} to ${response.body()?.results?.last()?.id}"
                ) else Log.d(
                    "MAIN",
                    "Repository get filtered location list for page-$currentPage from network : size ${response.body()?.results?.size}"
                )

                app_database.getLocationDao()
                    .insertAll(locationMapper.mapListToEntities(response.body()!!.results))
                return response.body()!!.results

            } else {
                return loadFromDatabase(currentPage, filters)
            }

        } catch (e: IOException) {
            return loadFromDatabase(currentPage, filters)
        }
    }

    private suspend fun loadFromDatabase(
        currentPage: Int,
        filters: HashMap<String, String>
    ): List<Location> {

        val result = if (filters.isEmpty()) getWithoutFilters(currentPage)
        else getWithFilters(currentPage, filters)

        return locationMapper.mapListFromEntities(result)
    }

    private suspend fun getWithFilters(
        currentPage: Int,
        filters: HashMap<String, String>
    ): List<LocationEntity> {

        val result = makeFilteredRequestToDatabase(currentPage - 1, filters)

        if (result != null && result.isNotEmpty()) {
            Log.d(
                "MAIN",
                "Repository get filtered Location list from database : size ${result.size} page: $currentPage"
            )

            hasNextPage.value =
                makeFilteredRequestToDatabase(currentPage, filters)?.isNotEmpty() == true

            return result

        } else {
            Log.d("MAIN", "Repository can't find filtered Location list for page : $currentPage!!!")
            throw Exception()
        }

    }

    private suspend fun makeFilteredRequestToDatabase(
        currentPage: Int,
        filters: HashMap<String, String>
    ): List<LocationEntity>? {

        return app_database.getLocationDao().getFilteredLocations(
            name = filters["name"] ?: "",
            dimension = filters["dimension"] ?: "",
            type = filters["type"] ?: "",
            start = currentPage * Constants.LOCATION_PAGE_SIZE,
            pageSize = Constants.LOCATION_PAGE_SIZE
        )
    }

    private suspend fun getWithoutFilters(currentPage: Int): List<LocationEntity> {
        val result = app_database.getLocationDao()
            .getLocations(
                pageSize = Constants.LOCATION_PAGE_SIZE,
                start = (currentPage - 1) * Constants.LOCATION_PAGE_SIZE
            )
        if (result != null && result.isNotEmpty()) {
            Log.d(
                "MAIN",
                "Repository get LocationList from database : since ${result.first().id} to ${result.last().id}"
            )

            hasNextPage.value = currentPage < app_database.getMaxPagesDao().getPagesEntity("location").page

            return result
        } else {
            Log.d("MAIN", "Repository can't find locationList for page : $currentPage!!!")
            throw Exception()
        }
    }
}
