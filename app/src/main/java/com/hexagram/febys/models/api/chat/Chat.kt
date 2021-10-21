package com.hexagram.febys.models.api.chat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val _id: String,
    val sender: Sender,
    val message: String,
    val from: String,
    val sent_time: String,
) : Parcelable