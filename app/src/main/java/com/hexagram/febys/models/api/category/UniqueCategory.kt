package com.hexagram.febys.models.api.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@kotlinx.parcelize.Parcelize
data class UniqueCategory(
    val image: List<String>,
    val _id: String,
    val name: String,
    val link: String,
    @SerializedName("category_id")
    val categoryId: Int
) : Parcelable
