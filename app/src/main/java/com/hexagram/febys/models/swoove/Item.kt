package com.hexagram.febys.models.swoove

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    @SerializedName("category_id")
    val categoryId: String?,
    @SerializedName("collect_cash")
    val collectCash: Boolean,
    @SerializedName("currency_code")
    val currencyCode: String,
    @SerializedName("currency_name")
    val currencyName: String,
    @SerializedName("currency_symbol")
    val currencySymbol: String,
    val description: String?,
    val dimensions: Dimensions,
    val itemCost: Int,
    val itemIcon: String,
    val itemImage: String,
    val itemName: String,
    val itemPrice: String?,
    val itemQuantity: Int,
    val itemWeight: Int
) : Parcelable