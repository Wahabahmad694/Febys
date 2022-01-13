package com.hexagram.febys.network

import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.response.ResponseRefreshToken
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface TokenRefreshService {
    @POST
    @FormUrlEncoded
    suspend fun refreshToken(
        @Url url: String, @FieldMap fields: Map<String, String>
    ): ApiResponse<ResponseRefreshToken>
}