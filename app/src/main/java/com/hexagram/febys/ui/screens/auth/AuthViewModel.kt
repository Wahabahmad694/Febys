package com.hexagram.febys.ui.screens.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.enum.SocialLogin
import com.hexagram.febys.models.api.consumer.Consumer
import com.hexagram.febys.models.api.profile.Profile
import com.hexagram.febys.models.api.subscription.Subscription
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestSignup
import com.hexagram.febys.network.response.ResponseLogin
import com.hexagram.febys.network.response.ResponseOtpVerification
import com.hexagram.febys.network.response.ResponseSignup
import com.hexagram.febys.repos.IAuthRepo
import com.hexagram.febys.ui.screens.payment.models.Wallet
import com.hexagram.febys.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo: IAuthRepo
) : BaseViewModel() {
    private val _observeSignupResponse = MutableLiveData<DataState<ResponseSignup>>()
    val observeSignupResponse: LiveData<DataState<ResponseSignup>> = _observeSignupResponse

    private val _observeOtpResponse = MutableLiveData<DataState<ResponseOtpVerification>>()
    val observeOtpResponse: LiveData<DataState<ResponseOtpVerification>> = _observeOtpResponse

    private val _observeLoginResponse = MutableLiveData<Event<DataState<ResponseLogin>>>()
    val observeLoginResponse: LiveData<Event<DataState<ResponseLogin>>> = _observeLoginResponse

    private val _observeProfileResponse = MutableLiveData<DataState<Profile?>>()
    val observeProfileResponse: LiveData<DataState<Profile?>> = _observeProfileResponse

    private val _observeResetCredentialResponse =
        MutableLiveData<DataState<Unit>>()
    val observeResetCredentialResponse: LiveData<DataState<Unit>> =
        _observeResetCredentialResponse

    fun signup(requestSignup: RequestSignup) = viewModelScope.launch {
        _observeSignupResponse.postValue(DataState.Loading())
        authRepo.signup(requestSignup).collect {
            _observeSignupResponse.postValue(it)
        }
    }

    fun verifyUser(otp: String) = viewModelScope.launch {
        _observeOtpResponse.postValue(DataState.Loading())
        authRepo.verifyUser(otp).collect {
            _observeOtpResponse.postValue(it)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _observeLoginResponse.postValue(Event(DataState.Loading()))
        authRepo.login(email, password).collect {
            _observeLoginResponse.postValue(Event(it))
        }
    }

    fun resetCredentials(email: String) = viewModelScope.launch {
        _observeResetCredentialResponse.postValue(DataState.Loading())
        authRepo.resetCredentials(email).collect {
            _observeResetCredentialResponse.postValue(it)
        }
    }

    fun fetchProfile() = viewModelScope.launch {
        delay(1000)
        authRepo.fetchUserProfile().collect {
            _observeProfileResponse.postValue(it)
        }
    }

    fun signOut(onSignOut: () -> Unit) {
        authRepo.signOut()
        onSignOut.invoke()
    }

    fun socialLogin(token: String, socialLogin: SocialLogin) = viewModelScope.launch {
        _observeLoginResponse.postValue(Event(DataState.Loading()))
        authRepo.socialLogin(token, socialLogin).collect {
            _observeLoginResponse.postValue(Event((it)))
        }
    }

    fun getConsumer(): Consumer? = authRepo.getConsumer()

    fun getWallet(): Wallet? = authRepo.getWallet()

    fun getSubscription(): Subscription? = authRepo.getSubscription()

}