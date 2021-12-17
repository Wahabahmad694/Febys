package com.hexagram.febys.models.api.filters

import android.os.Parcelable
import com.hexagram.febys.models.api.product.Attr
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attributes(
    val attributes: List<Attr>,
    val key: String
) : Parcelable