package com.hexagram.febys.models.api.menu

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoreMenusResponse(
    @SerializedName("__v")
    val version: Int,
    @SerializedName("_id")
    val id: String,
    val hamburgerMenu: List<StoreMenus>,
    val navigationMenu: List<StoreMenus>,
    val topMenu: List<StoreMenus>
) : Parcelable