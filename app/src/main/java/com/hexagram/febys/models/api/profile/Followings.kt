package com.hexagram.febys.models.api.profile

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Followings(
    @SerializedName("__v")
    val version: Int,
    @SerializedName("_id")
    val id: String,
    @SerializedName("consumer_id")
    val consumerId: Int,
    @SerializedName("created_at")
    val createdAt: String,
    val followings: List<String>,
    @SerializedName("updated_at")
    val updatedAt: String
):Parcelable