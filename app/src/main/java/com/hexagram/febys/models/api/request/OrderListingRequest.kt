package com.hexagram.febys.models.api.request

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderListingRequest constructor(
    val filters: Map<String, String>?
) : Parcelable