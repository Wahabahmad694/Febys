package com.hexagram.febys.models.swoove

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EstimateTypeDetails(
    val icon: String,
    val name: String
):Parcelable