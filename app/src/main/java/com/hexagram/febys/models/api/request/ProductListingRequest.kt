package com.hexagram.febys.models.api.request

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.hexagram.febys.ui.screens.product.filters.FiltersType
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductListingRequest constructor(
    var filterType: FiltersType? = null,
    var searchStr: String? = null,
    var isDefault: Boolean = false,
    var hasPromotion: Boolean = false,
    var trendsOnSale: Boolean = false,
    var editorsPick: Boolean = false,
    var categoryIds: MutableSet<Int> = mutableSetOf(),
    var vendorIds: MutableSet<String> = mutableSetOf(),
    var variantAttrs: MutableSet<String> = mutableSetOf(),
    var minPrice: Int? = null,
    var maxPrice: Int? = null,
    var sortByPrice: String? = null
) : Parcelable {
    companion object {
        private const val KEY_DEFAULT = "variants.default"
        private const val KEY_HAS_PROMOTION = "variants.has_promotion"
        private const val KEY_TRENDS_ON_SALE = "variants.trendsOnSale"
        private const val KEY_EDITORS_PICK = "editor_picked"
        private const val KEY_CATEGORY_IDS = "category_id"
        private const val KEY_VENDOR_IDS = "vendor_id"
        private const val KEY_ATTR = "variants.attributes.value"
        private const val KEY_PRICE = "variants.price.value"
        private const val KEY_GTE = "\$gte" // greater than equal to
        private const val KEY_LTE = "\$lte" // less than equal to
        private const val KEY_IN = "\$in"
        private const val KEY_OR = "\$or"
        const val KEY_ASC = "asc"
        const val KEY_DESC = "desc"
    }

    fun createFilters(): JsonObject {
        val filters = JsonObject()

        isDefault = variantAttrs.isEmpty()
                && filterType != FiltersType.TODAY_DEALS
                && filterType != FiltersType.UNDER_HUNDRED
                && filterType != FiltersType.TRENDING
                && filterType != FiltersType.SPECIAL_PRODUCT
        if (isDefault) filters.addProperty(KEY_DEFAULT, isDefault)

        if (categoryIds.isNotEmpty()) {
            val ids = JsonArray()
            categoryIds.forEach { ids.add(it) }
            val category = JsonObject()
            category.add(KEY_IN, ids)
            filters.add(KEY_CATEGORY_IDS, category)
        }

        if (vendorIds.isNotEmpty()) {
            val ids = JsonArray()
            vendorIds.forEach { ids.add(it) }
            val vendors = JsonObject()
            vendors.add(KEY_IN, ids)
            filters.add(KEY_VENDOR_IDS, vendors)
        }

        if (variantAttrs.isNotEmpty()) {
            val attrs = JsonArray()
            variantAttrs.forEach { attrs.add(it) }
            val variantsAttr = JsonObject()
            variantsAttr.add(KEY_IN, attrs)
            filters.add(KEY_ATTR, variantsAttr)
        }

        if (minPrice != null || maxPrice != null) {
            val priceRange = JsonObject()

            if (minPrice != null) priceRange.addProperty(KEY_GTE, minPrice)
            if (maxPrice != null) priceRange.addProperty(KEY_LTE, maxPrice)

            val price = JsonObject()
            price.add(KEY_PRICE, priceRange)

            val jsonArray = JsonArray()
            jsonArray.add(price)

            filters.add(KEY_OR, jsonArray)
        }

        if (hasPromotion) {
            filters.addProperty(KEY_HAS_PROMOTION, hasPromotion)
        }

        if (trendsOnSale) {
            filters.addProperty(KEY_TRENDS_ON_SALE, trendsOnSale)
        }
        if (editorsPick) {
            filters.addProperty(KEY_EDITORS_PICK, editorsPick)
        }

        return filters
    }

    fun createSorter(): JsonObject? {
        if (sortByPrice == null && !trendsOnSale) return null
        val sorter = JsonObject()
        if (sortByPrice != null) sorter.addProperty(KEY_PRICE, sortByPrice)
        if (trendsOnSale) sorter.addProperty("variants.stats.sales.value", "desc")
        return sorter
    }

    fun deepCopy(): ProductListingRequest {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, ProductListingRequest::class.java)
    }
}