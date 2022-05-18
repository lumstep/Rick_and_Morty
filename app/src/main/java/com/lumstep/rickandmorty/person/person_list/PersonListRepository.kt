package com.lumstep.rickandmorty.person.person_list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.lumstep.rickandmorty.*
import com.lumstep.rickandmorty.pages_data.PagesEntity
import com.lumstep.rickandmorty.person.Person
import com.lumstep.rickandmorty.person.PersonEntity
import com.lumstep.rickandmorty.person.PersonService
import java.io.IOException

class PersonListRepository(
    private val app_database: AppDatabase,
    private val personApi: PersonService,
    private val personMapper: Mapper<Person, PersonEntity>,
) : ListRepository<Person> {

    override var hasNextPage = MutableLiveData<Boolean>()

    override suspend fun getList(currentPage: Int, filters: HashMap<String, String>): List<Person> {

        try {
            val response =
                if (filters.isEmpty()) personApi.getPersonPage(currentPage)
                else {
                    personApi.getPersonFilteredList(
                        currentPage,
                        name = filters["name"],
                        status = filters["status"],
                        species = filters["species"],
                        type = filters["type"],
                        gender = filters["gender"]
                    )
                }

            if (response.isSuccessful) {
                app_database.getMaxPagesDao()
                    .insertMaxPages(PagesEntity("person", response.body()!!.info.pages))

                hasNextPage.value = response.body()!!.info.next != null

                if (filters.isEmpty()) Log.d(
                    "MAIN",
                    "Repository get personList for page-$currentPage from network : since ${response.body()?.results?.first()?.id} to ${response.body()?.results?.last()?.id}"
                ) else Log.d(
                    "MAIN",
                    "Repository get filtered person list for page-$currentPage from network : size ${response.body()?.results?.size}"
                )

                app_database.getPersonDao()
                    .insertAll(personMapper.mapListToEntities(response.body()!!.results))
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
        filters: HashMap<String, String>,
    ): List<Person> {

        val result = if (filters.isEmpty()) getWithoutFilters(currentPage)
        else getWithFilters(currentPage, filters)

        return personMapper.mapListFromEntities(result)
    }

    private suspend fun getWithFilters(
        currentPage: Int,
        filters: HashMap<String, String>,
    ): List<PersonEntity> {

        val result = makeFilteredRequestToDatabase(currentPage - 1, filters)

        if (result != null && result.isNotEmpty()) {
            Log.d(
                "MAIN",
                "Repository get filtered person list from database : size ${result.size} page: $currentPage"
            )

            hasNextPage.value =
                makeFilteredRequestToDatabase(currentPage, filters)?.isNotEmpty() == true

            return result

        } else {
            Log.d("MAIN", "Repository can't find filtered person list for page : $currentPage!!!")
            throw Exception()
        }

    }

    private suspend fun makeFilteredRequestToDatabase(
        currentPage: Int,
        filters: HashMap<String, String>,
    ): List<PersonEntity>? {

        return app_database.getPersonDao().getFilteredPersons(
            name = filters["name"] ?: "",
            species = filters["species"] ?: "",
            type = filters["type"] ?: "",
            status = if (filters.containsKey("status") && filters["status"] != null) listOf(
                filters["status"]!!
            )
            else listOf("Alive", "Dead", "Unknown"),
            gender = if (filters.containsKey("gender") && filters["gender"] != null) listOf(
                filters["gender"]!!
            ) else listOf(
                filters["gender"] ?: "Male", "Female", "Genderless", "Unknown"
            ),
            start = currentPage * Constants.PERSON_PAGE_SIZE,
            pageSize = Constants.PERSON_PAGE_SIZE
        )
    }

    private suspend fun getWithoutFilters(currentPage: Int): List<PersonEntity> {
        val result = app_database.getPersonDao()
            .getPersons(
                pageSize = Constants.PERSON_PAGE_SIZE,
                start = (currentPage - 1) * Constants.PERSON_PAGE_SIZE
            )
        return if (result != null && result.isNotEmpty()) {
            Log.d(
                "MAIN",
                "Repository get personList from database : since ${result.first().id} to ${result.last().id}"
            )

            hasNextPage.postValue(currentPage < app_database.getMaxPagesDao()
                .getPagesEntity("person").page)

            result
        } else {
            Log.d("MAIN", "Repository can't find personList for page : $currentPage!!!")
            throw Exception("There is no data in the cache. Try restarting your internet connection to download the data")
        }
    }
}
