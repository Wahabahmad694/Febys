package com.hexagram.febys.models.api.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Packaging(
    val weight: Int,
    val width: Int,
    val length: Int,
    val height: Int,
    val _id: String
) : Parcelable