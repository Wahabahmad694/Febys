package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Warranty(
    val _id: String,
    val applicable: Boolean,
    @SerializedName("duration_days")
    val durationDays: Int
) : Parcelable