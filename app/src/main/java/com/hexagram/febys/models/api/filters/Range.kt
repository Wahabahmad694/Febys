package com.hexagram.febys.models.api.filters

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Range(
    val max: Double,
    val min: Double
) : Parcelable