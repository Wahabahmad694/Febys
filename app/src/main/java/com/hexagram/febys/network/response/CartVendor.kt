package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class CartVendor(
    val id: Int,
    val name: String,
    @SerializedName("store_name")
    val storeName: String,
    val email: String,
    @SerializedName("phone_number")
    val phoneNo: String,
    @SerializedName("individual_vendor_type")
    val individualVendorType: String,
    val vendorAmount: Double,
    val items: List<CartItem>
)