package com.hexagram.febys.models.api.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attr(
    val _id: String,
    val name: String,
    val value: String
) : Parcelable