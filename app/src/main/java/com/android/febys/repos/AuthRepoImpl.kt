package com.android.febys.repos

import com.android.febys.R
import com.android.febys.database.dao.UserDao
import com.android.febys.dto.User
import com.android.febys.network.AuthService
import com.android.febys.network.DataState
import com.android.febys.network.adapter.*
import com.android.febys.network.requests.RequestSignup
import com.android.febys.network.response.ResponseLogin
import com.android.febys.network.response.ResponseOtpVerification
import com.android.febys.network.response.ResponseSignup
import com.android.febys.prefs.IPrefManger
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(
    private val service: AuthService,
    private val userDao: UserDao,
    private val pref: IPrefManger
) : IAuthRepo {

    override fun signup(
        signupReq: RequestSignup, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseSignup>> {
        return flow<DataState<ResponseSignup>> {
            service.signup(signupReq)
                .onSuccess {
                    val response = Gson().fromJson(data!!, ResponseSignup.Success::class.java)
                    val accessToken = data.get("user").asJsonObject["access_token"].asString
                    pref.saveAuthToken(accessToken)
                    saveUser(response.user)
                    emit(DataState.data(response))
                }
                .onError {
                    val errorBody = response.errorBody()?.string()

                    val dataState = if (errorBody.isNullOrEmpty()) {
                        DataState.error(message())
                    } else {
                        val errorResponse: ResponseSignup =
                            Gson().fromJson(errorBody, ResponseSignup.Fail::class.java)
                        DataState.data(errorResponse)
                    }

                    emit(dataState)
                }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    override fun verifyUser(
        otp: String, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseOtpVerification>> {
        return flow<DataState<ResponseOtpVerification>> {
            val user = userDao.getUser()
            val authToken = pref.getAuthToken()
            val verificationReq = mapOf("otp" to otp)
            service.verifyUser(authToken, verificationReq, user.id)
                .onSuccess {
                    val response =
                        Gson().fromJson(data!!, ResponseOtpVerification.Success::class.java)
                    updateUser(response.user)
                    emit(DataState.data(response))
                }
                .onError {
                    val errorBody = response.errorBody()?.string()

                    val dataState = if (errorBody.isNullOrEmpty()) {
                        DataState.error(message())
                    } else {
                        val errorResponse: ResponseOtpVerification =
                            Gson().fromJson(errorBody, ResponseOtpVerification.Fail::class.java)
                        DataState.data(errorResponse)
                    }

                    emit(dataState)
                }
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
                val response = Gson().fromJson(data!!, ResponseLogin.Success::class.java)
                val accessToken = data.get("user").asJsonObject["access_token"].asString
                pref.saveAuthToken(accessToken)
                saveUser(response.user)
                emit(DataState.data(response))
            }
                .onError {
                    val errorBody = response.errorBody()?.string()

                    val dataState = if (errorBody.isNullOrEmpty()) {
                        DataState.error(message())
                    } else {
                        val errorResponse: ResponseLogin =
                            Gson().fromJson(errorBody, ResponseLogin.Fail::class.java)
                        DataState.data(errorResponse)
                    }

                    emit(dataState)
                }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    override suspend fun saveUser(user: User) {
        userDao.insert(user)
    }

    override suspend fun updateUser(user: User) {
        userDao.update(user)
    }
}