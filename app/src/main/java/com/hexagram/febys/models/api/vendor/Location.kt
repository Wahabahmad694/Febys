package com.hexagram.febys.models.api.vendor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val _id: String,
    val type: String,
    val coordinates: List<Double>
) : Parcelable