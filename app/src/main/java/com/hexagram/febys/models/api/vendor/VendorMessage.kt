package com.hexagram.febys.models.api.vendor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VendorMessage(
    @SerializedName("vendor_id")
    val vendorId: String,
    var message: String
) : Parcelable