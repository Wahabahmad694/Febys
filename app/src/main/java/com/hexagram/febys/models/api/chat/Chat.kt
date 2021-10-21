package com.hexagram.febys.models.api.chat

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Chat(
    val _id: String,
    val sender: Sender,
    val message: String,
    val from: String,
    @SerializedName("sent_time")
    val sentTime: String,
) : Parcelable