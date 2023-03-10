package com.hexagram.febys.ui.screens.store.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.menu.StoreMenus
import com.hexagram.febys.models.api.menu.StoreTemplate
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IStoreMenusRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreListViewModel @Inject constructor(
    private val storeMenuRepo: IStoreMenusRepo
) : BaseViewModel() {
    private val _observeStoreMenu = MutableLiveData<DataState<List<StoreMenus>>>()
    val observeStoreMenus: LiveData<DataState<List<StoreMenus>>> = _observeStoreMenu

    private val _observeStoreTemplate = MutableLiveData<DataState<StoreTemplate>>()
    val observeStoreTemplate: LiveData<DataState<StoreTemplate>> = _observeStoreTemplate

    init {
        fetchStoreMenu()
    }

    fun fetchStoreMenu() = viewModelScope.launch {
        _observeStoreMenu.postValue(DataState.Loading())
        storeMenuRepo.fetchAllMenu().collect {
            _observeStoreMenu.postValue(it)
        }
    }

    fun fetchStoreTemplate(storeId: String) = viewModelScope.launch {
        _observeStoreTemplate.postValue(DataState.Loading())
        storeMenuRepo.fetchStoreTemplate(storeId).collect {
            _observeStoreTemplate.postValue(it)
        }
    }
}