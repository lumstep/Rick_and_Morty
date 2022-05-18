package com.lumstep.rickandmorty.person.person_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.lumstep.rickandmorty.Constants
import com.lumstep.rickandmorty.ListRepository
import com.lumstep.rickandmorty.person.Person
import kotlinx.coroutines.flow.Flow

class PersonListViewModel : ViewModel() {

    fun getPersonList(
        repository: ListRepository<Person>,
        filters: HashMap<String, String>
    ): Flow<PagingData<Person>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = Constants.PERSON_PAGE_SIZE,
                initialLoadSize = Constants.PERSON_PAGE_SIZE * 2
            )
        ) { PersonDataSource(repository, filters) }.flow.cachedIn(
            viewModelScope
        )
    }
}