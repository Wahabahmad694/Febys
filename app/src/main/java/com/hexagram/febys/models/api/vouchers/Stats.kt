package com.hexagram.febys.models.api.vouchers

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stats(
    @SerializedName("used_count")
    val usedCount: Int
):Parcelable
