package com.hexagram.febys.models.api.banners

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Banner(
    val image: List<String>,
    val _id: String,
    val type: String,
    @SerializedName("for")
    val _for: String,
    val name: String,
    @SerializedName("category_id")
    val categoryId: Int
) : Parcelable
