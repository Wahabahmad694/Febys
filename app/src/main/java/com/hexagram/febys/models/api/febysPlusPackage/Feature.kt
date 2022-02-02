package com.hexagram.febys.models.api.febysPlusPackage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Feature(
    val _id: String,
    val title: String
):Parcelable