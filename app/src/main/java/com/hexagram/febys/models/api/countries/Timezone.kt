package com.hexagram.febys.models.api.countries

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Timezone(
    val abbreviation: String,
    val gmtOffset: Int,
    val gmtOffsetName: String,
    val tzName: String,
    val zoneName: String
):Parcelable