package com.android.febys.models.responses

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Int,
    val parentId: Int?,
    val name: String,
    val slug: String,
    val icon: String,
    val logo: String,
    val enable: Int,
    val featured: Int,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("child_id")
    val childIds: String,
    @SerializedName("child_name")
    val childNames: String,
    @SerializedName("total_child")
    val totalChild: Int,
)
