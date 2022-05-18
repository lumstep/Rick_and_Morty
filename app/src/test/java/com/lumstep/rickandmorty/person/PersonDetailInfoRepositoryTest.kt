package com.lumstep.rickandmorty.person

import com.lumstep.rickandmorty.AppDatabase
import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.episode.EpisodeMapper
import com.lumstep.rickandmorty.episode.EpisodeService
import com.lumstep.rickandmorty.person.detail_info.PersonDetailInfoRepository
import com.lumstep.rickandmorty.person.person_list.PersonListRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
class PersonDetailInfoRepositoryTest {

    class MyException : Exception()

    private val personEntities = ArrayList<PersonEntity>()
    private val testDispatcher = TestCoroutineDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            personEntities.add(PersonEntity(id = id,
                name = id.toString(),
                status = null,
                species = null,
                type = null,
                gender = null,
                originName = null,
                originUrl = null,
                locationName = null,
                locationUrl = null,
                url = null,
                created = null,
                image = null,
                episode = null))
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
        val personDao = mockk<PersonDao>()
        val personApi = mockk<PersonService>()
        val episodeApi = mockk<EpisodeService>()

        every { database.getPersonDao() } returns personDao

        val response = Response.error<Person>(404, ResponseBody.create(null, ""))

        coEvery { personApi.getPerson(id) } returns response

        coEvery {
            personDao.getPerson(id)
        } throws MyException()

        PersonDetailInfoRepository(database,
            personApi,
            episodeApi,
            PersonMapper,
            EpisodeMapper).getDetailInfo(id)
    }

    //при наличии интернет соединения нельзя ничего брать с базы данных, а наоборот база данных обновляется через интернет
    @Test(expected = MyException::class)
    fun `getDetailInfoWithNetwork`() = runBlockingTest {
        val id = 1

        val database = mockk<AppDatabase>()
        val personApi = mockk<PersonService>()
        val episodeApi = mockk<EpisodeService>()

        every { database.getPersonDao() } throws AssertionError()

        coEvery { personApi.getPerson(id) } throws MyException()

        PersonDetailInfoRepository(database,
            personApi,
            episodeApi,
            PersonMapper,
            EpisodeMapper).getDetailInfo(id)
    }
}
