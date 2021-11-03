package com.hexagram.febys.models.api.cart

import com.google.gson.annotations.SerializedName

data class Cart(
    val _id: String,
    @SerializedName("vendor_products")
    val vendorProducts: MutableList<VendorProducts>
)
