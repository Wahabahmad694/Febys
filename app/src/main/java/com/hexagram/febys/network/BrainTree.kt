package com.hexagram.febys.network

import com.hexagram.febys.ui.screens.payment.models.brainTree.TokenResponse
import com.hexagram.febys.network.adapter.ApiResponse
import retrofit2.http.GET

interface BrainTree {

    @GET("v2/transaction/braintree/fetch-client-token")
    suspend fun getBraintreeToken(): ApiResponse<TokenResponse>

}