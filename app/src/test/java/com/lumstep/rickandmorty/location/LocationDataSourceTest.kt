package com.lumstep.rickandmorty.location

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.ListRepository
import com.lumstep.rickandmorty.location.location_list.LocationDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.Mockito
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class LocationDataSourceTest {

    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `getListReturnEmptyList`() = runBlockingTest {

        val dataSource = LocationDataSource(repository = object : ListRepository<Location> {
            override var hasNextPage = mock<MutableLiveData<Boolean>>()

            override suspend fun getList(
                currentPage: Int,
                filters: HashMap<String, String>,
            ): List<Location> {
                return ArrayList()
            }

        }, filters = HashMap<String, String>())

        val loadParams = mock<PagingSource.LoadParams<Int>>()

        Mockito.`when`(loadParams.key).thenReturn(null)

        val pageResult = dataSource.load(loadParams)

        Assertions.assertTrue(pageResult is PagingSource.LoadResult.Page<Int, Location>)
    }

    @Test
    fun `getListReturnFullList`() = runBlockingTest {
        val locations = ArrayList<Location>()
        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            locations.add(Location(id = id,
                name = id.toString(),
                type = null,
                dimension = null,
                url = null,
                created = null,
                residents = null))
        }

        val dataSource = LocationDataSource(repository = object : ListRepository<Location> {
            override var hasNextPage = mock<MutableLiveData<Boolean>>()

            override suspend fun getList(
                currentPage: Int,
                filters: HashMap<String, String>,
            ): List<Location> {
                hasNextPage.value = true
                return locations
            }
        }, filters = HashMap<String, String>())

        val loadParams = mock<PagingSource.LoadParams<Int>>()

        Mockito.`when`(loadParams.key).thenReturn(null)

        val pageResult = dataSource.load(loadParams)

        Assertions.assertTrue(pageResult is PagingSource.LoadResult.Page<Int, Location>)
    }

    @Test
    fun `getListReturnException`() = runBlockingTest {

        val dataSource = LocationDataSource(repository = object : ListRepository<Location> {
            override var hasNextPage = mock<MutableLiveData<Boolean>>()

            override suspend fun getList(
                currentPage: Int,
                filters: HashMap<String, String>,
            ): List<Location> {
                hasNextPage.value = true
                throw Exception()
            }
        }, filters = HashMap<String, String>())

        val loadParams = mock<PagingSource.LoadParams<Int>>()

        Mockito.`when`(loadParams.key).thenReturn(null)

        val pageResult = dataSource.load(loadParams)

        Assertions.assertTrue(pageResult is PagingSource.LoadResult.Error<Int, Location>)
    }
}