package com.lumstep.rickandmorty.episode.episode_list

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lumstep.rickandmorty.ListRepository
import com.lumstep.rickandmorty.episode.Episode


private const val STARTING_PAGE_INDEX = 1

class EpisodeDataSource(
    private val repository: ListRepository<Episode>,
    private val filters: HashMap<String, String>
) : PagingSource<Int, Episode>() {

    override fun getRefreshKey(state: PagingState<Int, Episode>): Int {
        return STARTING_PAGE_INDEX
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Episode> {
        return try {
            val currentPage: Int = params.key ?: STARTING_PAGE_INDEX

            val prevPage: Int? = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1
            var nextPage: Int? = null

            val episodes = repository.getList(currentPage, filters)

            repository.hasNextPage.observeForever {
                nextPage = if (it) currentPage + 1 else null
            }

            Log.d(
                "MAIN",
                "Episode PagingSource received list of episodes: page = $currentPage, prev = $prevPage, next = $nextPage"
            )

            LoadResult.Page(
                data = episodes,
                prevKey = prevPage,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            Log.d(
                "MAIN",
                "Episode PagingSource cannot received list of persons, because ${e.localizedMessage}"
            )
            LoadResult.Error(e)
        }
    }
}