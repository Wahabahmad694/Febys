package com.hexagram.febys.models.api.location

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationSuggestion(
    var name: String? = null,
    var lat: Double? = null,
    var lng: Double? = null,
    var address: LocationModel? = null
) : Parcelable