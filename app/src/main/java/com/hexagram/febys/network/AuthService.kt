package com.hexagram.febys.network

import com.hexagram.febys.models.api.profile.Profile
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.requests.RequestSignup
import com.hexagram.febys.network.response.ResponseLogin
import com.hexagram.febys.network.response.ResponseOtpVerification
import com.hexagram.febys.network.response.ResponseRefreshToken
import com.hexagram.febys.network.response.ResponseSignup
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

    @POST("v1/consumers/forgot-password")
    suspend fun resetCredentials(
        @Body email: Map<String, String>
    ): ApiResponse<Unit>

    @GET("v1/social/login")
    suspend fun socialLogin(@QueryMap reqLogin: Map<String, String>): ApiResponse<ResponseLogin>

    @GET("v1/consumers/me")
    suspend fun me(@Header("Authorization") authKey: String): ApiResponse<Profile>
}