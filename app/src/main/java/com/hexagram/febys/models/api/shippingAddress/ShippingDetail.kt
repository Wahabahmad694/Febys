package com.hexagram.febys.models.api.shippingAddress

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.contact.PhoneNo
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShippingDetail(
    @SerializedName("_id")
    val shippingDetailId: String?,
    val address: Address,
    val contact: PhoneNo,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("default")
    var isDefault: Boolean,
    @SerializedName("first_name")
    val firstName: String,
    val label: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("updated_at")
    val updatedAt: String
):Parcelable