package com.lumstep.rickandmorty.episode

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.ListRepository
import com.lumstep.rickandmorty.episode.episode_list.EpisodeDataSource
import com.lumstep.rickandmorty.person.person_list.PersonDataSource
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
class EpisodeDataSourceTest {

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

        val dataSource = EpisodeDataSource(repository = object : ListRepository<Episode> {
            override var hasNextPage = mock<MutableLiveData<Boolean>>()

            override suspend fun getList(
                currentPage: Int,
                filters: HashMap<String, String>,
            ): List<Episode> {
                return ArrayList()
            }

        }, filters = HashMap<String, String>())

        val loadParams = mock<PagingSource.LoadParams<Int>>()

        Mockito.`when`(loadParams.key).thenReturn(null)

        val pageResult = dataSource.load(loadParams)

        Assertions.assertTrue(pageResult is PagingSource.LoadResult.Page<Int, Episode>)
    }

    @Test
    fun `getListReturnFullList`() = runBlockingTest {
        val episodes = ArrayList<Episode>()
        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            episodes.add(Episode(id = id,
                name = id.toString(),
                air_date = null,
                episode = null,
                url = null,
                created = null,
                characters = null))
        }

        val dataSource = EpisodeDataSource(repository = object : ListRepository<Episode> {
            override var hasNextPage = mock<MutableLiveData<Boolean>>()

            override suspend fun getList(
                currentPage: Int,
                filters: HashMap<String, String>,
            ): List<Episode> {
                hasNextPage.value = true
                return episodes
            }
        }, filters = HashMap<String, String>())

        val loadParams = mock<PagingSource.LoadParams<Int>>()

        Mockito.`when`(loadParams.key).thenReturn(null)

        val pageResult = dataSource.load(loadParams)

        Assertions.assertTrue(pageResult is PagingSource.LoadResult.Page<Int, Episode>)
    }

    @Test
    fun `getListReturnException`() = runBlockingTest {

        val dataSource = EpisodeDataSource(repository = object : ListRepository<Episode> {
            override var hasNextPage = mock<MutableLiveData<Boolean>>()

            override suspend fun getList(
                currentPage: Int,
                filters: HashMap<String, String>,
            ): List<Episode> {
                hasNextPage.value = true
                throw Exception()
            }
        }, filters = HashMap<String, String>())

        val loadParams = mock<PagingSource.LoadParams<Int>>()

        Mockito.`when`(loadParams.key).thenReturn(null)

        val pageResult = dataSource.load(loadParams)

        Assertions.assertTrue(pageResult is PagingSource.LoadResult.Error<Int, Episode>)
    }
}