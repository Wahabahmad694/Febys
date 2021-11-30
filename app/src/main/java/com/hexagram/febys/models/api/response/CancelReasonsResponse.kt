package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.order.CancelReasons
import kotlinx.parcelize.Parcelize

@Parcelize
data class CancelReasonsResponse(
    @SerializedName("consumerReasons")
    val cancelReasons: CancelReasons
) : Parcelable