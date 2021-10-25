package com.hexagram.febys.models.api.template

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Template(
    val _id: String,
    val section: String,
    val images: List<TemplateImage>
) : Parcelable
