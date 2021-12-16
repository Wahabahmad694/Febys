package com.hexagram.febys.models.api.menu

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoreMenus(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val url: String?,
    val template: String?
) : Parcelable