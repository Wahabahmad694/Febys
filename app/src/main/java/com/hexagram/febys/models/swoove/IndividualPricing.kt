package com.hexagram.febys.models.swoove

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class IndividualPricing(
    @SerializedName("_id")
    val id: String,
    val contact: Contact,
    val dropOff: DropOff,
    @SerializedName("end_time")
    val endTime: Long,
    val items: List<Item>,
    val pickup: Pickup,
    @SerializedName("price_details")
    val priceDetails: PriceDetails,
    @SerializedName("start_time")
    val startTime: Long,
    @SerializedName("time_string")
    val timeString: String
):Parcelable