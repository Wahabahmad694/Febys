package com.android.febys.ui.screens.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.febys.base.BaseViewModel
import com.android.febys.dto.UserDTO
import com.android.febys.network.DataState
import com.android.febys.network.requests.RequestSignup
import com.android.febys.network.response.ResponseOtpVerification
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

    private val _observeOtpResponse = MutableLiveData<DataState<ResponseOtpVerification>>()
    val observeOtpResponse: LiveData<DataState<ResponseOtpVerification>> = _observeOtpResponse

    fun signup(requestSignup: RequestSignup) = viewModelScope.launch {
        _observeSignupResponse.postValue(DataState.loading())
        repo.signup(requestSignup).collect {
            _observeSignupResponse.postValue(it)
        }
    }

    fun saveUser(userDTO: UserDTO, onComplete: () -> Unit) = viewModelScope.launch {
        repo.saveUser(userDTO)
        onComplete.invoke()
    }

    fun verifyUser(otp: String) = viewModelScope.launch {
        _observeOtpResponse.postValue(DataState.loading())
        repo.verifyUser(otp).collect {
            _observeOtpResponse.postValue(it)
        }
    }

    fun updateUser(userDTO: UserDTO, onComplete: () -> Unit) = viewModelScope.launch {
        repo.updateUser(userDTO)
        onComplete.invoke()
    }
}