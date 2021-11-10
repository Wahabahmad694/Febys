package com.hexagram.febys.network

import com.hexagram.febys.R
import com.hexagram.febys.models.api.chat.Chat
import com.hexagram.febys.models.api.chat.Sender
import com.hexagram.febys.models.api.product.QAThread
import com.hexagram.febys.models.api.product.QuestionAnswers
import com.hexagram.febys.models.api.vouchers.Voucher
import com.hexagram.febys.models.view.*
import com.hexagram.febys.network.adapter.ApiResponse
import kotlinx.coroutines.delay
import retrofit2.Response

object FakeApiService {
    private val addresses = mutableListOf(
        ShippingAddress(
            1,
            "Hillary",
            "Widanama",
            "Home",
            "Ghana",
            "Virtual Incubator, Busy Internet",
            null,
            "Airport Res, Area",
            "Ghana",
            "233321",
            "03331234567",
            true
        ),
        ShippingAddress(
            2,
            "Hillary",
            "Widanama",
            "Office",
            "Ghana",
            "No. 18 Third Close, Airport Res Area",
            null,
            "Airport Res, Area",
            "Ghana",
            "233321",
            "03001234567",
            false
        )
    )

    suspend fun fetchShippingAddress(authToken: String): ApiResponse<List<ShippingAddress>> {
        delay(1000)
        return ApiResponse.ApiSuccessResponse(Response.success(addresses))
    }

    suspend fun setAsDefaultShippingAddress(id: Int): ShippingAddress? {
        delay(100)
        addresses.forEach { it.isDefault = false }
        val shippingAddress = addresses.firstOrNull { it.id == id }
        shippingAddress?.isDefault = true
        return shippingAddress
    }

    suspend fun updateShippingAddress(
        authToken: String, shippingAddress: ShippingAddress
    ): ApiResponse<Unit> {
        delay(100)
        val index = addresses.indexOfFirst { it.id == shippingAddress.id }
        if (index != -1) {
            addresses.removeAt(index)
            addresses.add(index, shippingAddress)
            if (shippingAddress.isDefault) {
                setAsDefaultShippingAddress(shippingAddress.id)
            }
        }
        return ApiResponse.ApiSuccessResponse(Response.success(Unit))
    }

    suspend fun addShippingAddress(
        authToken: String, shippingAddress: ShippingAddress
    ): ApiResponse<Unit> {
        delay(100)
        addresses.add(shippingAddress)
        if (shippingAddress.isDefault) setAsDefaultShippingAddress(shippingAddress.id)
        return ApiResponse.ApiSuccessResponse(Response.success(Unit))
    }

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