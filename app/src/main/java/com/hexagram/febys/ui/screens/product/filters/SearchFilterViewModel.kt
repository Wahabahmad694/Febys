package com.hexagram.febys.ui.screens.product.filters

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.filters.Filters
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.ISearchRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchFilterViewModel @Inject constructor(
    private val searchFilterRepo: ISearchRepo
) : BaseViewModel() {
    private val _observeFilters = MutableLiveData<DataState<Filters>>()
    val observeFilters: LiveData<DataState<Filters>> = _observeFilters

    init {
        fetchFilters()
    }

    fun fetchFilters() = viewModelScope.launch {
        _observeFilters.postValue(DataState.Loading())
        searchFilterRepo.fetchAllFilters().collect {
            _observeFilters.postValue(it)
        }
    }

}