package com.hexagram.febys.models.api.cart

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Courier(
    @SerializedName("tracking_id")
    val trackingId: String,
    val service: Service
) : Parcelable

@Parcelize
data class Service(
    val id: Int,
    val name: String,
    val url: String,
    val logo: String
) : Parcelable
