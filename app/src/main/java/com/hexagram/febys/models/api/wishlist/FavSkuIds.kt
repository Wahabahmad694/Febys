package com.hexagram.febys.models.api.wishlist

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavSkuIds @JvmOverloads constructor(
    @SerializedName("sku_ids", alternate = ["variant_sku_ids"])
    val skuIds: MutableSet<String> = mutableSetOf()
) : Parcelable
