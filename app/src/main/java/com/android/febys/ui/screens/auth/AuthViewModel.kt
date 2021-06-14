package com.android.febys.ui.screens.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.febys.base.BaseViewModel
import com.android.febys.dto.UserDTO
import com.android.febys.network.DataState
import com.android.febys.network.requests.RequestSignup
import com.android.febys.network.response.ResponseSignup
import com.android.febys.repos.IAuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: IAuthRepo
) : BaseViewModel() {
    private val _observeSignupResponse = MutableLiveData<DataState<ResponseSignup>>()
    val observeSignupResponse: LiveData<DataState<ResponseSignup>> = _observeSignupResponse


    fun signup(requestSignup: RequestSignup) = viewModelScope.launch {
        _observeSignupResponse.postValue(DataState.loading())
        repo.signup(requestSignup).collect {
            _observeSignupResponse.postValue(it)
        }
    }

    fun saveUser(userDTO: UserDTO, onComplete: () -> Unit) = viewModelScope.launch {
        repo.saveUser(userDTO)
    }
}