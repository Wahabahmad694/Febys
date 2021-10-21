package com.hexagram.febys.models.api.vendor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VendorStats(
    val products: Int,
    @SerializedName("units_sold")
    val unitsSold: Int
) : Parcelable