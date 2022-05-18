package com.lumstep.rickandmorty.location.location_list

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lumstep.rickandmorty.ListRepository
import com.lumstep.rickandmorty.location.Location


private const val STARTING_PAGE_INDEX = 1

class LocationDataSource(
    private val repository: ListRepository<Location>,
    private val filters: HashMap<String, String>
) : PagingSource<Int, Location>() {

    override fun getRefreshKey(state: PagingState<Int, Location>): Int {
        return STARTING_PAGE_INDEX
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Location> {
        return try {
            val currentPage: Int = params.key ?: STARTING_PAGE_INDEX

            val prevPage: Int? = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1
            var nextPage: Int? = null

            val locations = repository.getList(currentPage, filters)

            repository.hasNextPage.observeForever {
                nextPage = if (it) currentPage + 1 else null
            }

            Log.d(
                "MAIN",
                "Location PagingSource received list of locations: page = $currentPage, prev = $prevPage, next = $nextPage"
            )

            LoadResult.Page(
                data = locations,
                prevKey = prevPage,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            Log.d(
                "MAIN",
                "Location PagingSource cannot received list of locations, because ${e.localizedMessage}"
            )
            LoadResult.Error(e)
        }
    }
}