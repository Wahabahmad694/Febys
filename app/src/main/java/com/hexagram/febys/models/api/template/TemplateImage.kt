package com.hexagram.febys.models.api.template

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemplateImage(
    val _id: String,
    val url: String,
    val href: String
) : Parcelable