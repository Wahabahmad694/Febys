package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReturnReasonsResponse(
    @SerializedName("setting")
    val settings: ReturnReasons
) : Parcelable

@Parcelize
data class ReturnReasons(
    val id: Int,
    val name: String,
    @SerializedName("value")
    val _reasons: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
) : Parcelable {
    val reasons
        get() = _reasons.split(",")
}