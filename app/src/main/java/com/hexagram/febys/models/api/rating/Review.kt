package com.hexagram.febys.models.api.rating

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    var comment: String,
    val _id: String? = null
) : Parcelable