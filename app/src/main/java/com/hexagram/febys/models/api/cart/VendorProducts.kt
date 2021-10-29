package com.hexagram.febys.models.api.cart

import com.hexagram.febys.models.api.vendor.Vendor

data class VendorProducts(
    val vendor: Vendor,
    val products: MutableList<CartProduct>
)
