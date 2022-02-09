package com.hexagram.febys.ui.screens.febysPlus

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.febysPlusPackage.Package
import com.hexagram.febys.models.api.transaction.TransactionReq
import com.hexagram.febys.network.DataState
import com.hexagram.febys.repos.IFebysPlusRepo
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

    private val _subscribeFebysPackage = MutableLiveData<DataState<Unit>>()
    val observerSubscription: LiveData<DataState<Unit>> = _subscribeFebysPackage

    init {
        fetchFebysPackage()
    }

    fun fetchFebysPackage() = viewModelScope.launch {
        _observeFebysPackage.postValue(DataState.Loading())
        febysRepo.fetchFebysPackage().collect {
            _observeFebysPackage.postValue(it)
        }
    }

    fun subscribePackage(packageId: String, transactionReq: TransactionReq) = viewModelScope.launch {
        _subscribeFebysPackage.postValue(DataState.Loading())
        febysRepo.subscribePackage(packageId, transactionReq).collect {
            if (it is DataState.Data) fetchFebysPackage()
            else _subscribeFebysPackage.postValue(it)
        }
    }
}