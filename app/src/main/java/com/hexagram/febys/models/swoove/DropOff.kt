package com.hexagram.febys.models.swoove

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DropOff(
    @SerializedName("aggregation_center_id")
    val aggregationCenterId: String,
    val contact: Contact,
    @SerializedName("country_code")
    val countryCode: String,
    val lat: Double,
    val lng: Double,
    val location: String,
    @SerializedName("service_zone_id")
    val serviceZoneId: String,
    val type: String,
    val value: String?
):Parcelable