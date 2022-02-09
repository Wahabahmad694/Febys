package com.hexagram.febys.models.api.febysPlusPackage

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.price.Price
import kotlinx.parcelize.Parcelize

@Parcelize
data class Package(
    @SerializedName("_id")
    val id: String,
    val description: String,
    val features: List<Feature>,
    val price: Price,
    val icon: String?,
    val subscriptionDays: Int,
    val title: String,
    var selected: Boolean = false
) : Parcelable