package com.hexagram.febys.models.api.request

import android.os.Parcelable
import com.hexagram.febys.models.swoove.Estimate
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EstimateRequest(
    val estimate: Estimate
) : Parcelable