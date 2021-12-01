package com.hexagram.febys.models.api.order

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CancelReasons(
    val id: Int,
    val name: String,
    val value: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
) : Parcelable {
    val reasons: List<String>
        get() = value.split(",")
}
