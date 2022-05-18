package com.lumstep.rickandmorty.person

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.ListRepository
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
class PersonDataSourceTest {

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

        val dataSource = PersonDataSource(repository = object : ListRepository<Person> {
            override var hasNextPage = mock<MutableLiveData<Boolean>>()

            override suspend fun getList(
                currentPage: Int,
                filters: HashMap<String, String>,
            ): List<Person> {
                return ArrayList()
            }

        }, filters = HashMap<String, String>())

        val loadParams = mock<PagingSource.LoadParams<Int>>()

        Mockito.`when`(loadParams.key).thenReturn(null)

        val pageResult = dataSource.load(loadParams)

        Assertions.assertTrue(pageResult is PagingSource.LoadResult.Page<Int, Person>)
    }

    @Test
    fun `getListReturnFullList`() = runBlockingTest {
        val persons = ArrayList<Person>()
        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            persons.add(Person(id = id,
                name = id.toString(),
                status = null,
                species = null,
                type = null,
                gender = null,
                origin = null,
                location = null,
                url = null,
                created = null,
                image = null,
                episode = null))
        }

        val dataSource = PersonDataSource(repository = object : ListRepository<Person> {
            override var hasNextPage = mock<MutableLiveData<Boolean>>()

            override suspend fun getList(
                currentPage: Int,
                filters: HashMap<String, String>,
            ): List<Person> {
                hasNextPage.value = true
                return persons
            }
        }, filters = HashMap<String, String>())

        val loadParams = mock<PagingSource.LoadParams<Int>>()

        Mockito.`when`(loadParams.key).thenReturn(null)

        val pageResult = dataSource.load(loadParams)

        Assertions.assertTrue(pageResult is PagingSource.LoadResult.Page<Int, Person>)
    }

    @Test
    fun `getListReturnException`() = runBlockingTest {

        val dataSource = PersonDataSource(repository = object : ListRepository<Person> {
            override var hasNextPage = mock<MutableLiveData<Boolean>>()

            override suspend fun getList(
                currentPage: Int,
                filters: HashMap<String, String>,
            ): List<Person> {
                hasNextPage.value = true
                throw Exception()
            }
        }, filters = HashMap<String, String>())

        val loadParams = mock<PagingSource.LoadParams<Int>>()

        Mockito.`when`(loadParams.key).thenReturn(null)

        val pageResult = dataSource.load(loadParams)

        Assertions.assertTrue(pageResult is PagingSource.LoadResult.Error<Int, Person>)
    }
}