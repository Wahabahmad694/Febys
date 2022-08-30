package com.hexagram.febys.models.swoove

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SwooveEstimates(
    val code: Int,
    val message: String,
    val success: Boolean,
   val  responses: Responses
):Parcelable