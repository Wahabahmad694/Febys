package com.hexagram.febys.models.api.filters

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Range(
    val max: Int,
    val min: Int
) : Parcelable