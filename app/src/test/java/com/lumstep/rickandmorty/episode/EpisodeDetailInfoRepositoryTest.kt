package com.lumstep.rickandmorty.episode

import com.lumstep.rickandmorty.AppDatabase
import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.episode.EpisodeMapper
import com.lumstep.rickandmorty.episode.EpisodeService
import com.lumstep.rickandmorty.episode.detail_info.EpisodeDetailInfoRepository
import com.lumstep.rickandmorty.person.PersonMapper
import com.lumstep.rickandmorty.person.PersonService
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
class EpisodeDetailInfoRepositoryTest {

    class MyException : Exception()

    private val episodeEntities = ArrayList<EpisodeEntity>()
    private val testDispatcher = TestCoroutineDispatcher()


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            episodeEntities.add(EpisodeEntity(id = id,
                name = id.toString(),
                air_date = null,
                episode = null,
                url = null,
                created = null,
                characters = null))
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
        val episodeDao = mockk<EpisodeDao>()
        val episodeApi = mockk<EpisodeService>()
        val personApi = mockk<PersonService>()

        every { database.getEpisodeDao() } returns episodeDao

        val response = Response.error<Episode>(404, ResponseBody.create(null, ""))

        coEvery { episodeApi.getEpisode(id) } returns response

        coEvery {
            episodeDao.getEpisode(id)
        } throws MyException()

        EpisodeDetailInfoRepository(database,
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

        every { database.getEpisodeDao() } throws AssertionError()

        coEvery { episodeApi.getEpisode(id) } throws MyException()

        EpisodeDetailInfoRepository(database,
            personApi,
            episodeApi,
            PersonMapper,
            EpisodeMapper).getDetailInfo(id)
    }
}
