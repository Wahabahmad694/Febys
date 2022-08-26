package com.hexagram.febys.repos

import com.hexagram.febys.models.api.shippingAddress.ShippingAddress

interface ICheckoutRepo {
    fun getDefaultShippingAddress(): ShippingAddress?

}