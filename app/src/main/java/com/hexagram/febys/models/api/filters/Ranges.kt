package com.hexagram.febys.models.api.filters

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Ranges(
    @SerializedName("max_inclusive")
    val maxInclusive: Boolean,
    @SerializedName("products_count")
    val productsCount: Int,
    val range: Range
) : Parcelable