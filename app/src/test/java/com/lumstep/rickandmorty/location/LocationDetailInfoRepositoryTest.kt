package com.lumstep.rickandmorty.location

import com.lumstep.rickandmorty.AppDatabase
import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.location.detail_info.LocationDetailInfoRepository
import com.lumstep.rickandmorty.person.PersonMapper
import com.lumstep.rickandmorty.person.PersonService
import com.lumstep.rickandmorty.person.detail_info.PersonDetailInfoRepository
import io.mockk.*
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
class LocationDetailInfoRepositoryTest {

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
    fun `getDetailInfoWithoutNetwork`() = runBlockingTest {
        val id = 1

        val database = mockk<AppDatabase>()
        val locationDao = mockk<LocationDao>()
        val locationApi = mockk<LocationService>()
        val personApi = mockk<PersonService>()

        every { database.getLocationDao() } returns locationDao

        val response = Response.error<Location>(404, ResponseBody.create(null, ""))

        coEvery { locationApi.getLocation(id) } returns response

        coEvery {
            locationDao.getLocation(id)
        } throws MyException()

        LocationDetailInfoRepository(database,
            locationApi,
            personApi,
            LocationMapper,
            PersonMapper).getDetailInfo(id)
    }

    //при наличии интернет соединения нельзя ничего брать с базы данных, а наоборот база данных обновляется через интернет
    @Test(expected = MyException::class)
    fun `getDetailInfoWithNetwork`() = runBlockingTest {
        val id = 1

        val database = mockk<AppDatabase>()
        val personApi = mockk<PersonService>()
        val locationApi = mockk<LocationService>()

        every { database.getLocationDao() } throws AssertionError()

        coEvery { locationApi.getLocation(id) } throws MyException()

        LocationDetailInfoRepository(database,
            locationApi,
            personApi,
            LocationMapper ,
            PersonMapper).getDetailInfo(id)
    }
}
