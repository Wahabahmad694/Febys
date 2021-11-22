package com.hexagram.febys.models.api.cities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class City(
    val countryCode: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val stateCode: String
):Parcelable