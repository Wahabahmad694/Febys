package com.android.febys.network

import com.android.febys.network.adapter.ApiResponse
import com.android.febys.network.requests.RequestSignup
import com.android.febys.network.response.ResponseLogin
import com.android.febys.network.response.ResponseOtpVerification
import com.android.febys.network.response.ResponseSignup
import com.google.gson.JsonObject
import retrofit2.http.*

interface AuthService {
    @POST("v1/consumers")
    suspend fun signup(@Body requestSignup: RequestSignup): ApiResponse<ResponseSignup>

    @POST("v1/consumers/login")
    suspend fun login(@Body reqLogin: Map<String, String>): ApiResponse<ResponseLogin>

    @PATCH("v1/consumers/verify-otp")
    suspend fun verifyUser(
        @Header("Authorization") authToken: String,
        @Body otp: Map<String, String>
    ): ApiResponse<ResponseOtpVerification>
}