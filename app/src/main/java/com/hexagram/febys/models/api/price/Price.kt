package com.hexagram.febys.models.api.price

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Price(
    val _id: String,
    val value: Double,
    val currency: String
) : Parcelable