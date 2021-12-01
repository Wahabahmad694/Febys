package com.hexagram.febys.models.api.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.price.Price
import com.hexagram.febys.models.api.vendor.Vendor
import kotlinx.parcelize.Parcelize

@Parcelize
data class VendorProducts(
    val vendor: Vendor,
    val products: MutableList<CartProduct>,
    val amount: Price?,
    val status: String?,
    @SerializedName("return_fee_percentage")
    val return_fee_percentage: Double?,
    val reverted: Boolean?,
    @SerializedName("_id")
    val id: String,
    val courier: Courier?
) : Parcelable
