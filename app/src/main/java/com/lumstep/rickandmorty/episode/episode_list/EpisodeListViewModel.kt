package com.lumstep.rickandmorty.episode.episode_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.ListRepository
import com.lumstep.rickandmorty.episode.Episode
import kotlinx.coroutines.flow.Flow

class EpisodeListViewModel : ViewModel() {

    fun getEpisodeList(
        repository: ListRepository<Episode>,
        filters: HashMap<String, String>
    ): Flow<PagingData<Episode>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = Constants.EPISODE_PAGE_SIZE),
            pagingSourceFactory = { EpisodeDataSource(repository, filters) }).flow.cachedIn(
            viewModelScope
        )
    }
}