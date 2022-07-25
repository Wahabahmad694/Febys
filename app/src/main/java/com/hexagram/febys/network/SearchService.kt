package com.hexagram.febys.network

import com.hexagram.febys.models.api.request.SearchRequest
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.network.response.ResponseOfPagination
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface SearchService {

    @POST("v1/consumers/products")
    suspend fun searchProductsSuggestion(
        @QueryMap queryMap: Map<String, String>, @Body request: SearchRequest
    ): ApiResponse<ResponseOfPagination>

}