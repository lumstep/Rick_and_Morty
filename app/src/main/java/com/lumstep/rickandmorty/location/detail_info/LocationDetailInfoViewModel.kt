package com.lumstep.rickandmorty.location.detail_info

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumstep.rickandmorty.location.Location
import com.lumstep.rickandmorty.person.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.ArrayList

class LocationDetailInfoViewModel : ViewModel() {

    private lateinit var repository: LocationDetailRepository

    var locationDetailInfo = MutableLiveData<Location?>()
    var locationPersonList = MutableLiveData<List<Person>?>()

    fun updateLocation(id: Int) {

        val location = viewModelScope.async(Dispatchers.IO) {
            return@async repository.getDetailInfo(id)
        }
        viewModelScope.launch {
            locationDetailInfo.postValue(location.await())
        }
    }


    fun initRepository(rep : LocationDetailRepository) {
        repository =  rep
    }

    fun updatePersonList(persons: List<String>?) {

        if (persons != null && persons.isNotEmpty()) {

            val request = viewModelScope.async(Dispatchers.Default) {

                val request = mutableListOf<Int>()

                for (person in persons) {
                    val pageUri = Uri.parse(person)
                    pageUri.lastPathSegment?.toInt()?.let { request.add(it) }
                }
                return@async request
            }

            when (persons.size) {
                1 -> {
                    val person = viewModelScope.async(Dispatchers.IO) {
                        return@async repository.getPerson(request.await()[0])
                    }
                    viewModelScope.launch {
                        val result = person.await()
                        if (result != null) {
                            locationPersonList.postValue(listOf(result))
                        } else locationPersonList.postValue(null)                    }
                }
                else -> {
                    val personList = viewModelScope.async(Dispatchers.IO) {
                        return@async repository.getPersonList(request.await())
                    }
                    viewModelScope.launch {
                        locationPersonList.postValue(personList.await())
                    }
                }
            }


        } else locationPersonList.postValue(null)

    }
}