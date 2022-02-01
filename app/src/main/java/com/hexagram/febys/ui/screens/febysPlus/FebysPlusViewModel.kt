package com.hexagram.febys.ui.screens.febysPlus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IFebysPlusRepo
import com.hexagram.febys.models.api.febysPlusPackage.FebysPackageResponse
import com.hexagram.febys.models.api.febysPlusPackage.Package
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FebysPlusViewModel @Inject constructor(
    private val febysRepo: IFebysPlusRepo,
) : BaseViewModel() {
    private val _observeFebysPackage = MutableLiveData<DataState<List<Package>>>()
    val observeFebysPackage: LiveData<DataState<List<Package>>> = _observeFebysPackage

    init {
        fetchFebysPackage()
    }

    fun fetchFebysPackage() = viewModelScope.launch {
        _observeFebysPackage.postValue(DataState.Loading())
        febysRepo.fetchFebysPackage().collect {
            _observeFebysPackage.postValue(it)
        }
    }
}