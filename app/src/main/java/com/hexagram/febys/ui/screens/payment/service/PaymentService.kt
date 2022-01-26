package com.hexagram.febys.ui.screens.payment.service

import com.hexagram.febys.models.api.pagination.Pagination
import com.hexagram.febys.models.api.request.PagingListRequest
import com.hexagram.febys.models.api.request.PaymentRequest
import com.hexagram.febys.models.api.response.PaymentResponse
import com.hexagram.febys.network.adapter.ApiResponse
import com.hexagram.febys.ui.screens.payment.models.ConversionRateResponse
import com.hexagram.febys.ui.screens.payment.models.PayStackPaymentResponse
import com.hexagram.febys.ui.screens.payment.models.WalletResponse
import retrofit2.http.*

interface PaymentService {
    @GET("v1/payments/wallet/detail")
    suspend fun fetchWallet(@Header("Authorization") authKey: String): ApiResponse<WalletResponse>

    @POST("v1/payments/transaction/wallet")
    suspend fun doWalletPayment(
        @Header("Authorization") authToken: String,
        @Body paymentRequest: PaymentRequest
    ): ApiResponse<PaymentResponse>

    @POST("v1/payments/currency-conversion/rate")
    suspend fun fetchCurrencyConversionRate(
        @Header("Authorization") authToken: String,
        @Body conversionRequest: Map<String, String>
    ): ApiResponse<ConversionRateResponse>

    @POST("v1/payments/transaction/paystack")
    suspend fun doPayStackPayment(
        @Header("Authorization") authToken: String,
        @Body paymentRequest: PaymentRequest
    ): ApiResponse<PayStackPaymentResponse>

    @GET("v1/payments/transaction/paystack/{ref}")
    suspend fun listenToPayStackVerification(
        @Header("Authorization") authToken: String,
        @Path("ref") reference: String
    ): ApiResponse<PaymentResponse>

    @POST("v1/payments/transaction/paypal")
    suspend fun notifyPaypalPayment(
        @Header("Authorization") authToken: String,
        @Body req: Map<String, String>
    ): ApiResponse<PaymentResponse>

    @POST("v1/payments/transactions/listing")
    suspend fun fetchTransactions(
        @Header("Authorization") authToken: String,
        @QueryMap queryMap: Map<String, String>,
        @Body req: PagingListRequest
    ): ApiResponse<Pagination>
}