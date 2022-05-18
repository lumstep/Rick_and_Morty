package com.lumstep.rickandmorty.episode.detail_info

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumstep.rickandmorty.AppDatabase
import com.lumstep.rickandmorty.DetailInfoRepository
import com.lumstep.rickandmorty.episode.Episode
import com.lumstep.rickandmorty.person.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import java.util.ArrayList
import javax.inject.Inject

class EpisodeDetailInfoViewModel : ViewModel() {

    private lateinit var repository: EpisodeDetailRepository

    var episodeDetailInfo = MutableLiveData<Episode?>()
    var episodePersonList = MutableLiveData<List<Person>?>()

    fun updateEpisode(id: Int) {

        val episode = viewModelScope.async(Dispatchers.IO) {
            return@async repository.getDetailInfo(id)
        }
        viewModelScope.launch {
            episodeDetailInfo.postValue(episode.await())
        }
    }


    fun initRepository(rep: EpisodeDetailRepository) {
        repository = rep
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
                            episodePersonList.postValue(listOf(result))
                        } else episodePersonList.postValue(null)

                    }
                }
                else -> {
                    val personList = viewModelScope.async(Dispatchers.IO) {
                        return@async repository.getPersonList(request.await())
                    }
                    viewModelScope.launch {
                        episodePersonList.postValue(personList.await())
                    }
                }
            }


        } else episodePersonList.postValue(null)

    }
}