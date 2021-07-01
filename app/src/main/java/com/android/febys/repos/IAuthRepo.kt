package com.android.febys.repos

import com.android.febys.enum.SocialLogin
import com.android.febys.network.DataState
import com.android.febys.network.requests.RequestSignup
import com.android.febys.network.response.ResponseLogin
import com.android.febys.network.response.ResponseOtpVerification
import com.android.febys.network.response.ResponseRefreshToken
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

    fun refreshToken(dispatcher: CoroutineDispatcher = Dispatchers.IO): Flow<DataState<ResponseRefreshToken>>

    fun socialLogin(
        token: String, socialLogin: SocialLogin, dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): Flow<DataState<ResponseLogin>>

    fun signOut()
}