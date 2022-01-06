package com.hexagram.febys.models.api.filters

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.category.Category
import com.hexagram.febys.models.api.vendor.Vendor
import com.hexagram.febys.models.api.vendor.VendorPagingListing
import kotlinx.parcelize.Parcelize

@Parcelize
data class Filters(
    val attributes: Attributes,
    @SerializedName("available_categories")
    val availableCategories: List<Category>?,
    val category: Category,
    @SerializedName("price_range")
    val priceRange: PriceRange,
    @SerializedName("sibling_categories")
    val siblingCategories: List<Category>,
    val vendors: VendorPagingListing
) : Parcelable


