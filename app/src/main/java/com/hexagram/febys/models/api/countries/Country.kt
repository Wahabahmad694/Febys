package com.hexagram.febys.models.api.countries

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Country(
    val currency: String,
    val flag: String,
    val isoCode: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val phonecode: String,
    val timezones: List<Timezone>
):Parcelable