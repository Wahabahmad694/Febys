package com.hexagram.febys.network

import com.hexagram.febys.models.api.request.SearchRequest
import com.hexagram.febys.models.api.response.SearchSuggestionPagingListingResponse
import com.hexagram.febys.network.adapter.ApiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface SearchService {

    @POST("search")
    suspend fun searchProductsSuggestion(
        @QueryMap queryMap: Map<String, String>, @Body request: SearchRequest
    ): ApiResponse<SearchSuggestionPagingListingResponse>

}