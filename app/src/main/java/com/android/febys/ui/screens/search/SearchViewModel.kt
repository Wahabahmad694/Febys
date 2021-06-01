package com.android.febys.ui.screens.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.febys.base.BaseViewModel
import com.android.febys.repos.ISearchRepo
import com.android.febys.network.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: ISearchRepo
) : BaseViewModel() {
    private val _observeTabs = MutableLiveData<DataState<List<String>>>()
    val observeTabs: LiveData<DataState<List<String>>> = _observeTabs

    fun fetchAllCategories() = repo.fetchAllCategories(viewModelScope)

    fun fetchTabs() {
        viewModelScope.launch {
            _observeTabs.postValue(DataState.Loading())
            repo.fetchTabs().collect {
                _observeTabs.postValue(it)
            }

        }
    }
}