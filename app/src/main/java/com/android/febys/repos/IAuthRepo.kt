package com.android.febys.repos

import com.android.febys.network.DataState
import com.android.febys.network.requests.RequestSignup
import com.android.febys.network.response.ResponseLogin
import com.android.febys.network.response.ResponseOtpVerification
import com.android.febys.network.response.ResponseSignup
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface IAuthRepo {
    fun signup(
        signupReq: RequestSignup, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ResponseSignup>>

    fun verifyUser(
        otp: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ResponseOtpVerification>>


    fun login(
        email: String, password: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ResponseLogin>>

    fun resetCredentials(
        email: String, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<Unit>>
}