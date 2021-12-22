package com.hexagram.febys.models.api.vendor

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.role.Role
import kotlinx.parcelize.Parcelize

@Parcelize
data class Endorsement(
    val _id: String,
    val role: Role,
    val stats: EndorsementStats,
    val email: String,
    val active: Boolean,
    @SerializedName("business_info")
    val businessInfo: BusinessInfo,
    val name: String,
    val official: Boolean,
    @SerializedName("shop_name")
    val shopName: String,
    @SerializedName("template_photo")
    val templatePhoto: String,
    @SerializedName("created_at")
    val createdAt: String,
) : Parcelable


