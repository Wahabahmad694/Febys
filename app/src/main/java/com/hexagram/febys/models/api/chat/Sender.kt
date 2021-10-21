package com.hexagram.febys.models.api.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Sender(
    val _id: String,
    val name: String
) : Parcelable