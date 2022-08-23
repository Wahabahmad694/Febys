package com.hexagram.febys.models.api.location

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationModel(
    val city: String?,
    val state: String?,
    val country: String?,
    val postalCode: String?
) : Parcelable {
    fun fullAddress(): String {
        return "$postalCode, $city, $state ,$country"
    }
}