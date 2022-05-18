package com.lumstep.rickandmorty.person.detail_info

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lumstep.rickandmorty.episode.Episode
import com.lumstep.rickandmorty.person.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class PersonDetailInfoViewModel : ViewModel() {

    private lateinit var repository: PersonDetailRepository

    val personDetailInfo = MutableLiveData<Person?>()
    val personEpisodeList = MutableLiveData<List<Episode>?>()


    fun updatePerson(id: Int) {

        val person = viewModelScope.async(Dispatchers.IO) {
            return@async repository.getDetailInfo(id)
        }

        viewModelScope.launch {
            personDetailInfo.postValue(person.await())
        }
    }

    fun initRepository(rep: PersonDetailRepository) {
        repository = rep
    }

    fun updateEpisodeList(episodes: List<String>?) {

        if (episodes != null && episodes.isNotEmpty()) {

            val request = viewModelScope.async(Dispatchers.Default) {

                val request = mutableListOf<Int>()

                for (episode in episodes) {
                    val pageUri = Uri.parse(episode)
                    pageUri.lastPathSegment?.toInt()?.let { request.add(it) }
                }

                return@async request
            }


            when (episodes.size) {

                1 -> {
                    val episode = viewModelScope.async(Dispatchers.IO) {
                        return@async repository.getEpisode(request.await()[0])
                    }
                    viewModelScope.launch {
                        val result = episode.await()
                        if (result != null) {
                            personEpisodeList.postValue(listOf(result))
                        } else
                            personEpisodeList.postValue(null)
                    }
                }

                else -> {
                    val episodeList = viewModelScope.async(Dispatchers.IO) {
                        return@async repository.getEpisodeList(request.await())
                    }
                    viewModelScope.launch {
                        personEpisodeList.postValue(episodeList.await())
                    }
                }
            }

        } else personEpisodeList.postValue(null)
    }
}