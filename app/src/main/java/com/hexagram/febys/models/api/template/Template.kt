package com.hexagram.febys.models.api.template

import android.os.Parcelable
import com.hexagram.febys.models.api.filters.ExtraInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class Template(
    val _id: String,
    val section: String,
    val images: List<TemplateImage>,
    val extra_info: ExtraInfo,
    val logo: String,
    val logo_no: Int,
    val logo_type: String
) : Parcelable
