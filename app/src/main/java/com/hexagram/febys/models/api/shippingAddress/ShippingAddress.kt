package com.hexagram.febys.models.api.shippingAddress

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShippingAddress (
    @SerializedName("_id")
    val id: String?,
    @SerializedName("consumer_id")
    val consumerId: Int,
    @SerializedName("created_at")
    val creationTime: String,
    @SerializedName("shipping_detail")
    val shippingDetail: ShippingDetail,
    @SerializedName("updated_at")
    val updatedTime: String
) : Parcelable
