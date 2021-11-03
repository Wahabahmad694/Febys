package com.hexagram.febys.models.api.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Int,
    @SerializedName("parent_id")
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
    val children: List<Category>
) : Parcelable {
    val isEnable
        get() = enable == 1

    val isFeatured
        get() = featured == 1

    val hasChild
        get() = totalChild > 0
}
