package com.lumstep.rickandmorty

interface DetailInfoRepository<E> {
    suspend fun getDetailInfo(id: Int): E?
}