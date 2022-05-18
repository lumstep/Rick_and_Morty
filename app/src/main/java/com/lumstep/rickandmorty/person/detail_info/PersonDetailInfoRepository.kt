package com.lumstep.rickandmorty.person.detail_info

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

class PersonDetailInfoRepository(
    private val app_database: AppDatabase,
    private val personApi: PersonService,
    private val episodeApi: EpisodeService,
    private val personMapper: Mapper<Person, PersonEntity>,
    private val episodeMapper: Mapper<Episode, EpisodeEntity>,
) : PersonDetailRepository {

    override suspend fun getDetailInfo(id: Int): Person? {
        try {
            val response = personApi.getPerson(id)

            return if (response.isSuccessful) {
                Log.d(
                    "MAIN",
                    "Repository received person from network: id = ${response.body()?.id}"
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
                "Repository received person from database : id ${result.id}"
            )
            personMapper.mapFromEntity(result)
        } else {
            Log.d("MAIN", "Repository can't find detail info about person: id = $id!!!")
            null
        }
    }


    override suspend fun getEpisodeList(listOfEpisodeId: List<Int>): List<Episode>? {
        try {
            val response = episodeApi.getEpisodeList(listOfEpisodeId.toString())

            return if (response.isSuccessful) {
                Log.d(
                    "MAIN",
                    "Repository received episodeList from network: size= ${response.body()?.size}"
                )
                app_database.getEpisodeDao()
                    .insertAll(episodeMapper.mapListToEntities(response.body()!!))

                response.body()
            } else {
                loadEpisodeListFromDatabase(listOfEpisodeId)
            }

        } catch (e: IOException) {
            return loadEpisodeListFromDatabase(listOfEpisodeId)
        }
    }

    override suspend fun getEpisode(id: Int): Episode? {
        try {
            val response = episodeApi.getEpisode(id)

            return if (response.isSuccessful) {
                Log.d(
                    "MAIN",
                    "Repository received episodeList from network: size = 1"
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
                "Repository received episodeList from database: size = 1"
            )
            episodeMapper.mapFromEntity(result)

        } else {
            Log.d("MAIN", "Repository can't find episodeList for person!!!")
            null
        }
    }

    private suspend fun loadEpisodeListFromDatabase(listOfEpisodeId: List<Int>): List<Episode>? {
        val result = app_database.getEpisodeDao().getEpisodes(listOfEpisodeId)

        return if (result != null) {
            Log.d(
                "MAIN",
                "Repository received episodeList from database: size = ${result.size}"
            )
            episodeMapper.mapListFromEntities(result)

        } else {
            Log.d("MAIN", "Repository can't find episodeList for person!!!")
            null
        }
    }
}