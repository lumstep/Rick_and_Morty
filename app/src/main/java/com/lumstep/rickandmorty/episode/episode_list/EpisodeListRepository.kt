package com.lumstep.rickandmorty.episode.episode_list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.lumstep.rickandmorty.*
import com.lumstep.rickandmorty.episode.Episode
import com.lumstep.rickandmorty.episode.EpisodeEntity
import com.lumstep.rickandmorty.episode.EpisodeMapper
import com.lumstep.rickandmorty.episode.EpisodeService
import com.lumstep.rickandmorty.pages_data.PagesEntity
import okio.IOException

class EpisodeListRepository(
    private val app_database: AppDatabase,
    private val episodeApi: EpisodeService,
    private val episodeMapper: Mapper<Episode, EpisodeEntity>
) : ListRepository<Episode> {

    override var hasNextPage = MutableLiveData<Boolean>()

    override suspend fun getList(
        currentPage: Int,
        filters: HashMap<String, String>
    ): List<Episode> {

        try {
            val response =
                if (filters.isEmpty()) episodeApi.getEpisodePage(currentPage)
                else episodeApi.getEpisodeFilteredList(
                    currentPage,
                    name = filters["name"],
                    episode = filters["episode_number"],
                )

            return if (response.isSuccessful) {
                app_database.getMaxPagesDao().insertMaxPages(PagesEntity("episode", response.body()!!.info.pages))

                hasNextPage.value = response.body()?.info?.next != null

                if (filters.isEmpty()) Log.d(
                    "MAIN",
                    "Repository get episodeList from network : since ${response.body()?.results?.first()?.id} to ${response.body()?.results?.last()?.id}"
                ) else Log.d(
                    "MAIN",
                    "Repository get filtered episode list from network : size ${response.body()?.results?.size}"
                )

                app_database.getEpisodeDao()
                    .insertAll(EpisodeMapper.mapListToEntities(response.body()!!.results))
                response.body()!!.results

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
    ): List<Episode> {

        val result = if (filters.isEmpty()) getWithoutFilters(currentPage)
        else getWithFilters(currentPage, filters)

        return episodeMapper.mapListFromEntities(result)
    }

    private suspend fun getWithFilters(
        currentPage: Int,
        filters: HashMap<String, String>
    ): List<EpisodeEntity> {

        val result = makeFilteredRequestToDatabase(currentPage - 1, filters)

        if (result != null && result.isNotEmpty()) {
            Log.d(
                "MAIN",
                "Repository get filtered episode list from database : size ${result.size} page: $currentPage"
            )

            hasNextPage.value =
                makeFilteredRequestToDatabase(currentPage, filters)?.isNotEmpty() == true

            return result

        } else {
            Log.d("MAIN", "Repository can't find filtered episode list for page : $currentPage!!!")
            throw Exception()
        }
    }

    private suspend fun makeFilteredRequestToDatabase(
        currentPage: Int,
        filters: HashMap<String, String>
    ): List<EpisodeEntity>? {

        return app_database.getEpisodeDao().getFilteredEpisodes(
            name = filters["name"] ?: "",
            episode_number = filters["episode_number"] ?: "",
            start = currentPage * Constants.EPISODE_PAGE_SIZE,
            pageSize = Constants.EPISODE_PAGE_SIZE
        )

    }

    private suspend fun getWithoutFilters(currentPage: Int): List<EpisodeEntity> {

        val result = app_database.getEpisodeDao()
            .getEpisodes(
                pageSize = Constants.EPISODE_PAGE_SIZE,
                start = (currentPage - 1) * Constants.EPISODE_PAGE_SIZE
            )

        if (result != null && result.isNotEmpty()) {
            Log.d(
                "MAIN",
                "Repository get episodeList from database : since ${result.first().id} to ${result.last().id}"
            )

            hasNextPage.value = currentPage < app_database.getMaxPagesDao().getPagesEntity("episode").page

            return result
        } else {
            Log.d("MAIN", "Repository can't find episodeList for page : $currentPage!!!")
            throw Exception()
        }
    }
}
