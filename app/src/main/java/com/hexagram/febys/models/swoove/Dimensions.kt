package com.hexagram.febys.models.swoove

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dimensions(
    @SerializedName("size_id")
    val sizeId: String?,
    val x: Int,
    val y: Int,
    val z: Int
):Parcelable