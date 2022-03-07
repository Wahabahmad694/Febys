package com.hexagram.febys.models.api.notification

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationCount(
    @SerializedName("un_read")
    val unRead : Int,
    val badge: Int
):Parcelable
