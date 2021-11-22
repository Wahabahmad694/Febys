package com.hexagram.febys.repos

import com.hexagram.febys.models.api.shippingAddress.ShippingAddress
import com.hexagram.febys.models.view.PaymentMethod

interface ICheckoutRepo {
    fun getDefaultShippingAddress(): ShippingAddress?
    fun getDefaultPaymentMethod(): PaymentMethod?
}