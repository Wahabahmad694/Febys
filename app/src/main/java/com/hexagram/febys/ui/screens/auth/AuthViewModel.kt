package com.hexagram.febys.ui.screens.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.enum.SocialLogin
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestSignup
import com.hexagram.febys.network.response.ResponseLogin
import com.hexagram.febys.network.response.ResponseOtpVerification
import com.hexagram.febys.network.response.ResponseSignup
import com.hexagram.febys.repos.IAuthRepo
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

    private val _observeLoginResponse = MutableLiveData<DataState<ResponseLogin>>()
    val observeLoginResponse: LiveData<DataState<ResponseLogin>> = _observeLoginResponse

    private val _observeRefreshTokenResponse = MutableLiveData<DataState<Unit>>()
    val observeRefreshTokenResponse: LiveData<DataState<Unit>> = _observeRefreshTokenResponse

    private val _observeResetCredentialResponse =
        MutableLiveData<DataState<Unit>>()
    val observeResetCredentialResponse: LiveData<DataState<Unit>> =
        _observeResetCredentialResponse

    fun signup(requestSignup: RequestSignup) = viewModelScope.launch {
        _observeSignupResponse.postValue(DataState.Loading())
        repo.signup(requestSignup).collect {
            _observeSignupResponse.postValue(it)
        }
    }

    fun verifyUser(otp: String) = viewModelScope.launch {
        _observeOtpResponse.postValue(DataState.Loading())
        repo.verifyUser(otp).collect {
            _observeOtpResponse.postValue(it)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _observeLoginResponse.postValue(DataState.Loading())
        repo.login(email, password).collect {
            if (it != it) _observeLoginResponse.postValue(it)
        }
    }

    fun resetCredentials(email: String) = viewModelScope.launch {
        _observeResetCredentialResponse.postValue(DataState.Loading())
        repo.resetCredentials(email).collect {
            _observeResetCredentialResponse.postValue(it)
        }
    }

    fun refreshToken() = viewModelScope.launch {
        repo.refreshToken().collect {
            _observeRefreshTokenResponse.postValue(it)
        }
    }

    fun signOut(onSignOut: () -> Unit) {
        repo.signOut()
        onSignOut.invoke()
    }

    fun socialLogin(token: String, socialLogin: SocialLogin) = viewModelScope.launch {
        _observeLoginResponse.postValue(DataState.Loading())
        repo.socialLogin(token, socialLogin).collect {
            _observeLoginResponse.postValue(it)
        }
    }
}