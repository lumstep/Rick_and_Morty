package com.lumstep.rickandmorty.location.detail_info

import com.lumstep.rickandmorty.DetailInfoRepository
import com.lumstep.rickandmorty.location.Location
import com.lumstep.rickandmorty.person.Person

interface LocationDetailRepository:DetailInfoRepository<Location> {
    suspend fun getPersonList(listOfPersonId : List<Int>): List<Person>?
    suspend fun getPerson(id : Int): Person?
}