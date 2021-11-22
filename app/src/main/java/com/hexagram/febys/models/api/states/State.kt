package com.hexagram.febys.models.api.states

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class State(
    val countryCode: String,
    val isoCode: String,
    val latitude: String,
    val longitude: String,
    val name: String
):Parcelable