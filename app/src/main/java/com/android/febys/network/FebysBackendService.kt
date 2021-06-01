package com.android.febys.network

import com.android.febys.network.requests.RequestAllCategories
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface FebysBackendService {
    @POST("v1/categories/all")
    suspend fun fetchAllCategories(@Body req: Map<String, RequestAllCategories>): JsonObject
}