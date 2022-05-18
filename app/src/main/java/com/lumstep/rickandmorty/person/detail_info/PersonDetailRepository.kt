package com.lumstep.rickandmorty.person.detail_info

import com.lumstep.rickandmorty.DetailInfoRepository
import com.lumstep.rickandmorty.episode.Episode
import com.lumstep.rickandmorty.person.Person

interface PersonDetailRepository : DetailInfoRepository<Person> {
    suspend fun getEpisodeList(listOfEpisodeId: List<Int>): List<Episode>?
    suspend fun getEpisode(id: Int): Episode?
}