package com.lumstep.rickandmorty.episode.detail_info

import com.lumstep.rickandmorty.DetailInfoRepository
import com.lumstep.rickandmorty.episode.Episode
import com.lumstep.rickandmorty.person.Person

interface EpisodeDetailRepository:DetailInfoRepository<Episode> {
    suspend fun getPersonList(listOfPersonId : List<Int>): List<Person>?
    suspend fun getPerson(id : Int): Person?
}