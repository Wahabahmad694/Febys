package com.android.febys.network

import com.android.febys.network.adapter.ApiResponse
import com.android.febys.network.requests.RequestSignup
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("v1/consumers")
    suspend fun signup(@Body requestSignup: RequestSignup): ApiResponse<JsonObject>
}