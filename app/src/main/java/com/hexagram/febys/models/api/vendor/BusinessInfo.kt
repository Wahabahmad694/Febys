package com.hexagram.febys.models.api.vendor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BusinessInfo(
    val logo: String,
    val address: String,
    @SerializedName("vendor_type")
    val vendorType: String,
    val location: Location
) : Parcelable