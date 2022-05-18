package com.lumstep.rickandmorty.location.detail_info

import android.util.Log
import com.lumstep.rickandmorty.AppDatabase
import com.lumstep.rickandmorty.Mapper
import com.lumstep.rickandmorty.location.Location
import com.lumstep.rickandmorty.location.LocationEntity
import com.lumstep.rickandmorty.location.LocationService
import com.lumstep.rickandmorty.person.Person
import com.lumstep.rickandmorty.person.PersonEntity
import com.lumstep.rickandmorty.person.PersonService
import okio.IOException

class LocationDetailInfoRepository(
    private val app_database: AppDatabase,
    private val locationApi: LocationService,
    private val personApi: PersonService,
    private val locationMapper: Mapper<Location, LocationEntity>,
    private val personMapper: Mapper<Person, PersonEntity>,
) : LocationDetailRepository {


    override suspend fun getPersonList(listOfPersonId: List<Int>): List<Person>? {
        try {
            val response = personApi.getPersonList(listOfPersonId.toString())
            return if (response.isSuccessful) {

                Log.d(
                    "MAIN",
                    "Repository received personList from network: size= ${response.body()?.size}"
                )

                app_database.getPersonDao()
                    .insertAll(personMapper.mapListToEntities(response.body()!!))

                response.body()
            } else {
                loadPersonListFromDatabase(listOfPersonId)
            }

        } catch (e: IOException) {
            return loadPersonListFromDatabase(listOfPersonId)
        }
    }

    private suspend fun loadPersonListFromDatabase(listOfPersonId: List<Int>): List<Person>? {
        val result = app_database.getPersonDao().getPersons(listOfPersonId)

        return if (result != null) {
            Log.d(
                "MAIN",
                "Repository received personList from database: size = ${result.size}"
            )
            personMapper.mapListFromEntities(result)

        } else {
            Log.d("MAIN", "Repository can't find personList for episode!!!")
            null
        }
    }

    override suspend fun getPerson(id: Int): Person? {
        try {
            val response = personApi.getPerson(id)
            return if (response.isSuccessful) {
                Log.d(
                    "MAIN",
                    "Repository received personList from network: size = 1"
                )
                app_database.getPersonDao()
                    .insertPerson(personMapper.mapToEntity(response.body()!!))
                response.body()
            } else {
                loadPersonFromDatabase(id)
            }
        } catch (e: IOException) {
            return loadPersonFromDatabase(id)
        }
    }

    private suspend fun loadPersonFromDatabase(id: Int): Person? {
        val result = app_database.getPersonDao().getPerson(id)

        return if (result != null) {
            Log.d(
                "MAIN",
                "Repository received personList from database: size = 1"
            )
            personMapper.mapFromEntity(result)

        } else {
            Log.d("MAIN", "Repository can't find personList for person!!!")
            null
        }
    }


    override suspend fun getDetailInfo(id: Int): Location? {
        try {
            val response = locationApi.getLocation(id)

            return if (response.isSuccessful) {
                Log.d(
                    "MAIN",
                    "Repository received location from network: id = ${response.body()?.id}"
                )

                app_database.getLocationDao()
                    .insertLocation(locationMapper.mapToEntity(response.body()!!))
                response.body()

            } else {
                loadLocationFromDatabase(id)
            }

        } catch (e: IOException) {
            return loadLocationFromDatabase(id)
        }
    }

    private suspend fun loadLocationFromDatabase(id: Int): Location? {

        val result = app_database.getLocationDao().getLocation(id)

        return if (result != null) {
            Log.d(
                "MAIN",
                "Repository received location from database : id ${result.id}"
            )

            locationMapper.mapFromEntity(result)

        } else {
            Log.d("MAIN", "Repository can't find detail info about location: id = $id!!!")
            null
        }
    }
}