package com.hexagram.febys.models.api.rating

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Rating(
    val _id: String?,
    val score: Double,
    val count: Int
) : Parcelable
