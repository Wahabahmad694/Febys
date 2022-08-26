package com.hexagram.febys.models.swoove

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Responses(
    val estimates: List<Estimate>,
    @SerializedName("optimal_estimate")
    val optimalEstimate: OptimalEstimate,
    val payment_channel: String,
    var selectedEstimate: Estimate
):Parcelable