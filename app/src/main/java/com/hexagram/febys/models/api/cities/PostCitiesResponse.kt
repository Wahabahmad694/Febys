package com.hexagram.febys.models.api.cities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostCitiesResponse(
    val cities: List<City>
):Parcelable