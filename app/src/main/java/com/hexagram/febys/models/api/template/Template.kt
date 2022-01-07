package com.hexagram.febys.models.api.template

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.filters.ExtraInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class Template(
    val _id: String,
    val section: String,
    val images: List<TemplateImage>,
    @SerializedName("extra_info")
    val extraInfo: ExtraInfo,
    val logo: String,
    @SerializedName("logo_no")
    val logoNo: Int,
    @SerializedName("logo_type")
    val logoType: String
) : Parcelable
