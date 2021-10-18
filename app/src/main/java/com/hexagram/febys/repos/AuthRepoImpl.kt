package com.hexagram.febys.repos

import com.hexagram.febys.BuildConfig
import com.hexagram.febys.dataSource.IUserDataSource
import com.hexagram.febys.enum.SocialLogin
import com.hexagram.febys.network.AuthService
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.FebysBackendService
import com.hexagram.febys.network.adapter.*
import com.hexagram.febys.network.requests.RequestSignup
import com.hexagram.febys.network.response.*
import com.hexagram.febys.prefs.IPrefManger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val authService: AuthService,
    private val backendService: FebysBackendService,
    private val pref: IPrefManger,
    private val userDataSource: IUserDataSource,
    private val cartRepo: ICartRepo
) : IAuthRepo {

    override fun signup(
        signupReq: RequestSignup, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseSignup>> {
        return flow<DataState<ResponseSignup>> {
            authService.signup(signupReq)
                .onSuccess {
                    data!!.apply {
                        saveUserAndToken(user)
                        emit(DataState.Data(this))
                    }
                }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun verifyUser(
        otp: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseOtpVerification>> {
        return flow<DataState<ResponseOtpVerification>> {
            val authToken = pref.getAccessToken()
            val verificationReq = mapOf("otp" to otp)
            authService.verifyUser(authToken, verificationReq)
                .onSuccess {
                    emit(DataState.Data(data!!))
                }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun login(
        email: String, password: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseLogin>> {
        return flow<DataState<ResponseLogin>> {
            val loginReq = mapOf("email" to email, "password" to password)
            authService.login(loginReq).onSuccess {
                data!!.apply {
                    saveUserAndToken(user)
                    fetchUserDataOnLogin()
                    emit(DataState.Data(data))
                }
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun resetCredentials(
        email: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<Unit>> {
        return flow<DataState<Unit>> {
            val resetCredentialReq = mapOf("email" to email)
            authService.resetCredentials(resetCredentialReq)
                .onSuccess { emit(DataState.Data(Unit)) }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun refreshToken(dispatcher: CoroutineDispatcher): Flow<DataState<Unit>> {
        return flow<DataState<Unit>> {
            val refreshToken = userDataSource.getRefreshToken()
            if (refreshToken.isEmpty()) {
                emit(DataState.Data(Unit))
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

            authService.refreshToken(url, fields)
                .onSuccess {
                    data!!.apply {
                        userDataSource.saveAccessToken(accessToken)
                        userDataSource.saveRefreshToken(this.refreshToken)
                        fetchUserDataOnLogin()
                        emit(DataState.Data(Unit))
                    }
                }
                .onError {
                    signOut()
                    // for go to home screen, no need to display error msg
                    emit(DataState.Data(Unit))
                }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    override fun socialLogin(
        token: String, socialLogin: SocialLogin, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseLogin>> {
        return flow<DataState<ResponseLogin>> {
            val socialLoginReq = mapOf(
                "access_token" to token,
                "type" to socialLogin.name.lowercase(),
                "client" to "android"
            )

            authService.socialLogin(socialLoginReq).onSuccess {
                data!!.apply {
                    saveUserAndToken(user)
                    fetchUserDataOnLogin()
                    emit(DataState.Data(this))
                }
            }
                .onError { emit(DataState.ApiError(message)) }
                .onException { emit(DataState.ExceptionError()) }
                .onNetworkError { emit(DataState.NetworkError()) }
        }.flowOn(dispatcher)
    }

    private fun saveUserAndToken(user: User) {
        userDataSource.saveUser(user)
        userDataSource.saveAccessToken(user.accessToken ?: "")
        userDataSource.saveRefreshToken(user.refreshToken ?: "")
    }

    private suspend fun fetchUserDataOnLogin() {
        fetchWishListIds()
        fetchCart()
    }

    private suspend fun fetchCart() {
        cartRepo.pullAndPushCart()
    }

    private suspend fun fetchWishListIds() {
        val authToken = pref.getAccessToken()
        val response = backendService.fetchWishlistIds(authToken)
        if (response is ApiResponse.ApiSuccessResponse) {
            val fav = response.data!!.skuIds
            pref.saveFav(fav.toMutableSet())
        }
    }

    override fun signOut() {
        pref.clearFav()
        cartRepo.clearCart()
        userDataSource.clearUserState()
        userDataSource.clearUserData()
    }

    override fun getUser(): User? {
        return userDataSource.getUser()
    }
}