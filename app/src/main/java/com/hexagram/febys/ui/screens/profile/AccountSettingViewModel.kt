package com.hexagram.febys.ui.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestUpdateUser
import com.hexagram.febys.repos.IProfileRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountSettingViewModel @Inject constructor(
    private val profileRepo: IProfileRepo
) : BaseViewModel() {
    private val _observeProfile = MutableLiveData<DataState<Consumer>>()
    val observeProfile: LiveData<DataState<Consumer>> = _observeProfile

    private val _updateProfile = MutableLiveData<DataState<Consumer>>()
    val updateProfile: LiveData<DataState<Consumer>> = _updateProfile

    init {
        fetchProfile()
    }

    private fun fetchProfile() = viewModelScope.launch {
        _observeProfile.postValue(DataState.Loading())
        profileRepo.fetchProfile().collect {
            _observeProfile.postValue(it)
        }
    }

    fun updateProfile(requestUpdateUser: RequestUpdateUser) = viewModelScope.launch {
        _updateProfile.postValue(DataState.Loading())
        profileRepo.updateProfile(requestUpdateUser).collect {
            _updateProfile.postValue(it)
        }
    }
}