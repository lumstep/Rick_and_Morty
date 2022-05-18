package com.lumstep.rickandmorty.episode

import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.Mapper
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions

class EpisodeMapperTest {

    private lateinit var mapper: Mapper<Episode, EpisodeEntity>
    private val episodes = ArrayList<Episode>()
    private lateinit var episode: Episode
    private val episodeEntities = ArrayList<EpisodeEntity>()
    private lateinit var entity: EpisodeEntity

    @Before
    fun setup() {
        mapper = EpisodeMapper

        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            episodes.add(Episode(id = id,
                name = id.toString(),
                air_date = null,
                characters = null,
                url = null,
                created = null,
                episode = null))
        }

        for (id in 0..Constants.PERSON_PAGE_SIZE) {
            episodeEntities.add(EpisodeEntity(id = id,
                name = id.toString(),
                air_date = null,
                characters = null,
                url = null,
                created = null,
                episode = null))
        }

        episode = Episode(id = 0,
            name = "Stepan",
            air_date = "18.05.2022",
            characters = null,
            url = null,
            created = null,
            episode = null)

        entity = EpisodeEntity(
            id = 0,
            name = "Stepan",
            air_date = "18.05.2022",
            characters = null,
            url = null,
            created = null,
            episode = null
        )
    }

    @Test
    fun `getListEntities`() {

        val result = mapper.mapListToEntities(episodes)

        Assertions.assertTrue(result == episodeEntities)
    }

    @Test
    fun `getListDates`() {

        val result = mapper.mapListFromEntities(episodeEntities)

        Assertions.assertTrue(result == episodes)
    }

    @Test
    fun `getEntity`() {

        val result = mapper.mapToEntity(episode)

        Assertions.assertTrue(result == entity)
    }

    @Test
    fun `getData`() {

        val result = mapper.mapFromEntity(entity)

        Assertions.assertTrue(result == episode)
    }
}