package com.lumstep.rickandmorty.location

import com.lumstep.rickandmorty.AppDatabase
import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.location.location_list.LocationListRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@ExperimentalCoroutinesApi
class LocationListRepositoryTest {

    class MyException : Exception()

    private val locationEntities = ArrayList<LocationEntity>()
    private val testDispatcher = TestCoroutineDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            locationEntities.add(LocationEntity(id = id,
                name = id.toString(),
                type = null,
                dimension = null,
                url = null,
                created = null,
                residents = null))
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test(expected = MyException::class)
    fun `getListWithoutNetwork`() = runBlockingTest {
        val page = 1

        val database = mockk<AppDatabase>()
        val locationDao = mockk<LocationDao>()
        val locationApi = mockk<LocationService>()

        every { database.getLocationDao() } returns locationDao

        val response = Response.error<LocationList>(404, ResponseBody.create(null, ""))

        coEvery { locationApi.getLocationPage(page) } returns response

        coEvery {
            locationDao.getLocations(Constants.LOCATION_PAGE_SIZE,
                (page - 1) * Constants.LOCATION_PAGE_SIZE)
        } throws MyException()

        LocationListRepository(database, locationApi, LocationMapper).getList(page,
            HashMap())
    }

    //при наличии интернет соединения нельзя ничего брать с базы данных, а наоборот база данных обновляется через интернет
    @Test(expected = MyException::class)
    fun `getListWithNetwork`() = runBlockingTest {
        val page = 1

        val database = mockk<AppDatabase>()
        val locationApi = mockk<LocationService>()

        every { database.getLocationDao() } throws AssertionError()

        coEvery { locationApi.getLocationPage(page) } throws MyException()

        LocationListRepository(database, locationApi, LocationMapper).getList(page,
            HashMap())
    }
}
