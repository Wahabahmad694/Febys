package com.hexagram.febys.models.api.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val _id: String,
    @SerializedName("vendor_products")
    val vendorProducts: MutableList<VendorProducts>
):Parcelable
