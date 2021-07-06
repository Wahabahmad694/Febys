package com.hexagram.febys.repos

import com.hexagram.febys.enum.SocialLogin
import com.hexagram.febys.network.DataState
import com.hexagram.febys.network.requests.RequestSignup
import com.hexagram.febys.network.response.ResponseLogin
import com.hexagram.febys.network.response.ResponseOtpVerification
import com.hexagram.febys.network.response.ResponseRefreshToken
import com.hexagram.febys.network.response.ResponseSignup
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