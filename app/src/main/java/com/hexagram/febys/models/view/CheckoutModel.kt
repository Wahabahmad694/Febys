package com.hexagram.febys.models.view

import com.hexagram.febys.models.api.shippingAddress.ShippingAddress

data class CheckoutModel(
    var shippingAddress: ShippingAddress?,
    var paymentMethod: PaymentMethod?,
)