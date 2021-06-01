package com.android.febys.network

import com.android.febys.models.requests.RequestAllCategories
import com.google.gson.JsonObject
import retrofit2.http.Body
import retrofit2.http.POST

interface FebysService {
    @POST("v1/categories/all")
    suspend fun fetchAllCategories(@Body req: Map<String, RequestAllCategories>): JsonObject
}