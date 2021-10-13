package com.hexagram.febys.repos

import com.hexagram.febys.models.view.PaymentMethod
import com.hexagram.febys.models.view.ShippingAddress

interface ICheckoutRepo {
    fun getDefaultShippingAddress(): ShippingAddress?
    fun getDefaultPaymentMethod(): PaymentMethod?
}