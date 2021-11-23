package com.hexagram.febys.models.api.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Packaging(
    val weight: Double,
    val width: Double,
    val length: Double,
    val height: Double,
    val _id: String
) : Parcelable