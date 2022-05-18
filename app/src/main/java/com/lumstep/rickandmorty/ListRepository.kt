package com.lumstep.rickandmorty

import androidx.lifecycle.MutableLiveData

interface ListRepository <T> {
    suspend fun getList(currentPage: Int, filters: HashMap<String, String>): List<T>
    var hasNextPage : MutableLiveData<Boolean>
}