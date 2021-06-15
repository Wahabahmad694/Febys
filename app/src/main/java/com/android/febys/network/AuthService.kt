package com.android.febys.network

import com.android.febys.network.adapter.ApiResponse
import com.android.febys.network.requests.RequestSignup
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthService {
    @POST("v1/consumers")
    suspend fun signup(@Body requestSignup: RequestSignup): ApiResponse<JsonObject>

    @PATCH("v1/consumers/verify-otp/{userId}")
    suspend fun verifyUser(
        @Body otp: Map<String, String>,
        @Path("userId") userId: Int
    ): ApiResponse<JsonObject>
}