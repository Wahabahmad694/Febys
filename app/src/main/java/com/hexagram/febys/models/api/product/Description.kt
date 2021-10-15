package com.hexagram.febys.models.api.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Description(
    val _id: String,
    val title: String,
    val content: String
) : Parcelable