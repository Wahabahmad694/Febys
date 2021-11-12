package com.hexagram.febys.models.api.shippingAddress

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShippingAddressResponse(
    @SerializedName("shipping_details")
    val shippingDetails: List<ShippingAddress>
):Parcelable