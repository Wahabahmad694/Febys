package com.hexagram.febys.models.api.countries

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountryResponse(
    val countries: List<Country>
):Parcelable