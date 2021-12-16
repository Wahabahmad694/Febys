package com.hexagram.febys.models.api.rating

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val comment: String,
    val _id: String
) : Parcelable