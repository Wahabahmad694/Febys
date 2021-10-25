package com.hexagram.febys.models.api.social

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Social(
    val _id: String,
    val name: String,
    val url: String
) : Parcelable