package com.hexagram.febys.models.api.shippingAddress

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Coordinates(
    val coordinates: MutableList<Double?>
) : Parcelable
