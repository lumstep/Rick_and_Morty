package com.lumstep.rickandmorty.episode.detail_info

import android.util.Log
import com.lumstep.rickandmorty.AppDatabase
import com.lumstep.rickandmorty.Mapper
import com.lumstep.rickandmorty.episode.Episode
import com.lumstep.rickandmorty.episode.EpisodeEntity
import com.lumstep.rickandmorty.episode.EpisodeService
import com.lumstep.rickandmorty.person.Person
import com.lumstep.rickandmorty.person.PersonEntity
import com.lumstep.rickandmorty.person.PersonService
import okio.IOException

class EpisodeDetailInfoRepository(
    private val app_database: AppDatabase,
    private val personApi: PersonService,
    private val episodeApi: EpisodeService,
    private val personMapper: Mapper<Person, PersonEntity>,
    private val episodeMapper: Mapper<Episode, EpisodeEntity>,
) : EpisodeDetailRepository {


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


    override suspend fun getDetailInfo(id: Int): Episode? {
        try {
            val response = episodeApi.getEpisode(id)

            return if (response.isSuccessful) {
                Log.d(
                    "MAIN",
                    "Repository received episode from network: id = ${response.body()?.id}"
                )

                app_database.getEpisodeDao()
                    .insertEpisode(episodeMapper.mapToEntity(response.body()!!))
                response.body()

            } else {
                loadEpisodeFromDatabase(id)
            }

        } catch (e: IOException) {
            return loadEpisodeFromDatabase(id)
        }
    }

    private suspend fun loadEpisodeFromDatabase(id: Int): Episode? {

        val result = app_database.getEpisodeDao().getEpisode(id)

        return if (result != null) {
            Log.d(
                "MAIN",
                "Repository received episode from database : id ${result.id}"
            )

            episodeMapper.mapFromEntity(result)

        } else {
            Log.d("MAIN", "Repository can't find detail info about episode: id = $id!!!")
            null
        }
    }
}