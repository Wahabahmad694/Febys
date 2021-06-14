package com.android.febys.repos

import com.android.febys.R
import com.android.febys.database.dao.UserDao
import com.android.febys.dto.UserDTO
import com.android.febys.network.AuthService
import com.android.febys.network.DataState
import com.android.febys.network.adapter.*
import com.android.febys.network.requests.RequestSignup
import com.android.febys.network.response.ResponseOtpVerification
import com.android.febys.network.response.ResponseSignup
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AuthRepoImpl(
    private val service: AuthService,
    private val userDao: UserDao
) : IAuthRepo {

    override fun signup(
        signupReq: RequestSignup, dispatcher: CoroutineDispatcher
    ): Flow<DataState<ResponseSignup>> {
        return flow<DataState<ResponseSignup>> {
            service.signup(signupReq)
                .onSuccess {
                    emit(
                        DataState.data(Gson().fromJson(data!!, ResponseSignup.Success::class.java))
                    )
                }
                .onError {
                    val errorBody = response.errorBody()?.string()
                    emit(
                        if (errorBody.isNullOrEmpty()) {
                            DataState.error(message())
                        } else {
                            DataState.data(
                                Gson().fromJson(
                                    errorBody, ResponseSignup.Fail::class.java
                                )
                            )
                        }
                    )
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
            val verificationReq = mapOf("otp" to otp)
            service.verifyUser(verificationReq, user.id)
                .onSuccess {
                    emit(
                        DataState.data(
                            Gson().fromJson(data!!, ResponseOtpVerification.Success::class.java)
                        )
                    )
                }
                .onError {
                    val errorBody = response.errorBody()?.string()
                    emit(
                        if (errorBody.isNullOrEmpty()) {
                            DataState.error(message())
                        } else {
                            DataState.data(
                                Gson().fromJson(
                                    errorBody, ResponseOtpVerification.Fail::class.java
                                )
                            )
                        }
                    )
                }
                .onException { emit(DataState.error(R.string.error_something_went_wrong)) }
                .onNetworkError { emit(DataState.error(R.string.error_no_network_connected)) }
        }.flowOn(dispatcher)
    }

    override suspend fun saveUser(userDTO: UserDTO) {
        userDao.insert(userDTO)
    }

    override suspend fun updateUser(userDTO: UserDTO) {
        userDao.update(userDTO)
    }
}