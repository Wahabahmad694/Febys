package com.hexagram.febys.models.api.cateogry

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class UniqueCategory(
    val image: List<String>,
    val _id: String,
    val name: String,
    val link: String
) : Parcelable
