package com.hexagram.febys.network.requests

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.consumer.Consumer
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseUpdateUser constructor(
    @SerializedName("user")
    val user: Consumer
): Parcelable
