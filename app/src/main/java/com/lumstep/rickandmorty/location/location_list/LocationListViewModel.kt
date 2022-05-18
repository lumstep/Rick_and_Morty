package com.lumstep.rickandmorty.location.location_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.ListRepository
import com.lumstep.rickandmorty.location.Location
import kotlinx.coroutines.flow.Flow

class LocationListViewModel : ViewModel() {

    fun getLocationList(
        repository: ListRepository<Location>,
        filters: HashMap<String, String>
    ): Flow<PagingData<Location>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = Constants.LOCATION_PAGE_SIZE,
                initialLoadSize = Constants.LOCATION_PAGE_SIZE * 2
            )
        ) { LocationDataSource(repository, filters) }.flow.cachedIn(
            viewModelScope
        )
    }
}