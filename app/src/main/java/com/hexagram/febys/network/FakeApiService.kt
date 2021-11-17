package com.hexagram.febys.network

import com.hexagram.febys.R
import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.network.adapter.ApiResponse
import kotlinx.coroutines.delay
import retrofit2.Response

object FakeApiService {

    private val paymentMethods = mutableListOf(
        PaymentMethod(1, "RavePay", R.drawable.ic_rave_pay, true),
        PaymentMethod(2, "Mobile Money", R.drawable.ic_mobile_money, false),
        PaymentMethod(3, "Paypal", R.drawable.ic_paypal, false),
        PaymentMethod(4, "Cash on delivery", R.drawable.ic_cash_on_delivery, false)
    )

    suspend fun fetchPaymentMethods(): ApiResponse<List<PaymentMethod>> {
        delay(100)
        return ApiResponse.ApiSuccessResponse(Response.success(paymentMethods))
    }

    suspend fun setAsDefaultPaymentMethod(id: Int): PaymentMethod? {
        delay(100)
        paymentMethods.forEach { it.isDefault = false }
        val paymentMethod = paymentMethods.firstOrNull { it.id == id }
        paymentMethod?.isDefault = true
        return paymentMethod
    }
}