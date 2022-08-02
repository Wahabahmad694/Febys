package com.hexagram.febys.network

import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.ui.screens.payment.models.brainTree.TokenResponse
import com.hexagram.febys.ui.screens.payment.models.feeSlabs.FeeSlabRequest
import com.hexagram.febys.ui.screens.payment.models.feeSlabs.FeeSlabsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BrainTree {

    @GET("v2/transaction/braintree/fetch-client-token")
    suspend fun getBraintreeToken(): ApiResponse<TokenResponse>

    @POST("/api/v1/payments/fee-slabs")
    suspend fun feeSlabs(
        @Body request: FeeSlabRequest
    ): ApiResponse<FeeSlabsResponse>


}