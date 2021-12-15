package com.hexagram.febys.models.api.filters

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PriceRange(
    val key: String,
    val ranges: List<Ranges>
) : Parcelable