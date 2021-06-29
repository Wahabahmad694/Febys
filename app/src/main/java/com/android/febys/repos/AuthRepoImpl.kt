package com.android.febys.repos

import com.android.febys.BuildConfig
import com.android.febys.R
import com.android.febys.dataSource.IUserDataSource
import com.android.febys.dto.User
import com.android.febys.network.AuthService
import com.android.febys.network.DataState
import com.android.febys.network.adapter.*
import com.android.febys.network.requests.RequestSignup
import com.android.febys.network.response.ResponseLogin
import com.android.febys.network.response.ResponseOtpVerification
import com.android.febys.network.response.ResponseRefreshToken
import com.android.febys.network.response.ResponseSignup
import com.android.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val service: AuthService,
    private val pref: IPrefManger,
    private val userDataSource: IUserDataSource
) : IAuthRepo {

    override fun signup(
        signupReq: RequestSignup, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseSignup>> {
        return flow<DataState<ResponseSignup>> {
            service.signup(signupReq)
                .onSuccess {
                    data!!.apply {
                        saveUserAndToken(user)
                        emit(DataState.data(this))
                    }
                }
                .onError { emit(DataState.error(responseErrorMessage())) }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    override fun verifyUser(
        otp: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseOtpVerification>> {
        return flow<DataState<ResponseOtpVerification>> {
            val authToken = pref.getAccessToken()
            val verificationReq = mapOf("otp" to otp)
            service.verifyUser(authToken, verificationReq)
                .onSuccess {
                    data!!.apply {
                        saveUserAndToken(user)
                        emit(DataState.data(this))
                    }
                }
                .onError { emit(DataState.error(responseErrorMessage())) }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    override fun login(
        email: String, password: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseLogin>> {
        return flow<DataState<ResponseLogin>> {
            val loginReq = mapOf("email" to email, "password" to password)
            service.login(loginReq).onSuccess {
                data!!.apply {
                    saveUserAndToken(user)
                    emit(DataState.data(this))
                }
            }
                .onError { emit(DataState.error(responseErrorMessage())) }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    override fun resetCredentials(
        email: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<Unit>> {
        return flow<DataState<Unit>> {
            val resetCredentialReq = mapOf("email" to email)
            service.resetCredentials(resetCredentialReq)
                .onSuccess { emit(DataState.data(Unit)) }
                .onError { emit(DataState.error(responseErrorMessage())) }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    override fun refreshToken(dispatcher: CoroutineDispatcher): Flow<DataState<ResponseRefreshToken>> {
        return flow<DataState<ResponseRefreshToken>> {
            val refreshToken = userDataSource.getRefreshToken()
            if (refreshToken.isEmpty()) {
                emit(DataState.error(""))
                return@flow
            }

            val fields = mapOf(
                "grant_type" to "refresh_token",
                "client_id" to BuildConfig.keycloakClientId,
                "refresh_token" to refreshToken,
                "client_secret" to BuildConfig.keycloakClientSecret
            )
            val url =
                "https://auth.qa.febys.com/auth/realms/febys-consumers/protocol/openid-connect/token"

            service.refreshToken(url, fields)
                .onSuccess {
                    data!!.apply {
                        userDataSource.saveAccessToken(accessToken)
                        userDataSource.saveRefreshToken(this.refreshToken)
                        emit(DataState.data(this))
                    }
                }
                .onError { emit(DataState.error(responseErrorMessage())) }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    private fun saveUserAndToken(user: User) {
        userDataSource.saveUser(user)
        userDataSource.saveAccessToken(user.accessToken ?: "")
        userDataSource.saveRefreshToken(user.refreshToken ?: "")
    }

    override fun signOut() {
        userDataSource.clearUserState()
        userDataSource.clearUserData()
    }
}