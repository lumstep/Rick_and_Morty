package com.lumstep.rickandmorty.person.person_list

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lumstep.rickandmorty.ListRepository
import com.lumstep.rickandmorty.person.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest


private const val STARTING_PAGE_INDEX = 1

class PersonDataSource(
    private val repository: ListRepository<Person>,
    private val filters: HashMap<String, String>
) : PagingSource<Int, Person>() {

    override fun getRefreshKey(state: PagingState<Int, Person>): Int {
        return STARTING_PAGE_INDEX
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Person> {
        return try {
            val currentPage: Int = params.key ?: STARTING_PAGE_INDEX

            val prevPage: Int? = if (currentPage == STARTING_PAGE_INDEX) null else currentPage - 1
            var nextPage: Int? = null

            val persons = repository.getList(currentPage, filters)

            repository.hasNextPage.observeForever {
                nextPage = if (it) currentPage + 1 else null
            }

            Log.d(
                "MAIN",
                "Person PagingSource received list of persons: page = $currentPage, prev = $prevPage, next = $nextPage"
            )

            LoadResult.Page(
                data = persons,
                prevKey = prevPage,
                nextKey = nextPage
            )

        } catch (e: Exception) {
            Log.d(
                "MAIN",
                "Person PagingSource cannot received list of persons, because ${e.localizedMessage}"
            )
            LoadResult.Error(e)

        }
    }

}