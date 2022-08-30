package com.hexagram.febys.models.swoove

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pickup(
    val aggregation_center_id: String,
    val contact: Contact,
    val country_code: String,
    val lat: Double,
    val lng: Double,
    val location: String,
    val service_zone_id: String,
    val type: String,
    val value: String?
):Parcelable