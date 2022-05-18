package com.lumstep.rickandmorty.episode

import com.lumstep.rickandmorty.Mapper
import java.lang.StringBuilder


object EpisodeMapper : Mapper<Episode, EpisodeEntity> {
    override fun mapToEntity(episode: Episode): EpisodeEntity {
        val personIdList =
            if (episode.characters != null && episode.characters.isNotEmpty()) {
                val s = StringBuilder()
                for (episodeId in episode.characters) {
                    s.append(episodeId).append(",")
                }
                s.deleteCharAt(s.lastIndex)
                s.toString()
            } else null
        return EpisodeEntity(
            id = episode.id,
            name = episode.name,
            air_date = episode.air_date,
            episode = episode.episode,
            characters = personIdList,
            url = episode.url,
            created = episode.created
        )
    }

    override fun mapFromEntity(episode: EpisodeEntity): Episode {
        val personIdList = episode.characters?.split(",")

        return Episode(
            id = episode.id,
            name = episode.name,
            air_date = episode.air_date,
            episode = episode.episode,
            characters = personIdList,
            url = episode.url,
            created = episode.created
        )
    }

    override fun mapListToEntities(episodes: List<Episode>): List<EpisodeEntity> {
        val episodesEntityList = ArrayList<EpisodeEntity>()
        for (episode in episodes) {
            episodesEntityList.add(mapToEntity(episode))
        }
        return episodesEntityList
    }

    override fun mapListFromEntities(episodes: List<EpisodeEntity>): List<Episode> {
        val episodesList = ArrayList<Episode>()
        for (episode in episodes) {
            episodesList.add(mapFromEntity(episode))
        }
        return episodesList    }
}