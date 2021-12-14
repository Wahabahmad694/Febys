package com.hexagram.febys.models.api.stats

import android.os.Parcelable
import com.hexagram.febys.models.api.rating.Rating
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stats(
    val rating: Rating
) : Parcelable
