package com.hexagram.febys.models.api.shippingAddress

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.contact.PhoneNo
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostShippingDetail constructor(
    val id: String?,
    val address: Address,
    val contact: PhoneNo,
    @SerializedName("first_name")
    val firstName: String,
    val label: String,
    @SerializedName("last_name")
    val lastName: String,
) : Parcelable