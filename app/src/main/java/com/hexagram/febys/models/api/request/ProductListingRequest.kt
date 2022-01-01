package com.hexagram.febys.models.api.request

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductListingRequest constructor(
    var searchStr: String? = null,
    var isDefault: Boolean = false,
    var categoryId: Int? = null,
    var vendorId: String? = null,
    var variantAttrs: MutableMap<String, String> = mutableMapOf(),
    var minPrice: Int? = null,
    var maxPrice: Int? = null
) : Parcelable {
    companion object {
        private const val KEY_DEFAULT = "variants.default"
        private const val KEY_CATEGORY_IDS = "category_id"
        private const val KEY_VENDOR_IDS = "vendor_id"
        private const val KEY_ATTR = "variants.attributes.value"
        private const val KEY_PRICE = "variants.price.value"
        private const val KEY_GTE = "\$gte" // greater than equal to
        private const val KEY_LT = "\$lt" // less than
        private const val KEY_IN = "\$in"
        private const val KEY_OR = "\$or"
    }

    fun createRequest(): JsonObject {
        val filters = JsonObject()

        if (isDefault) filters.addProperty(KEY_DEFAULT, isDefault)

        if (categoryId != null) {
            val ids = JsonArray()
            ids.add(categoryId)
            val category = JsonObject()
            category.add(KEY_IN, ids)
            filters.add(KEY_CATEGORY_IDS, category)
        }

        if (vendorId != null) {
            val ids = JsonArray()
            ids.add(vendorId)
            val vendors = JsonObject()
            vendors.add(KEY_IN, ids)
            filters.add(KEY_VENDOR_IDS, vendors)
        }

        if (variantAttrs.isNotEmpty()) {
            val attrs = JsonArray()
            variantAttrs.forEach { attrs.add(it.value) }
            val variantsAttr = JsonObject()
            variantsAttr.add(KEY_IN, attrs)
            filters.add(KEY_ATTR, variantsAttr)
        }

        if (minPrice != null && maxPrice != null) {
            val priceRange = JsonObject()
            priceRange.addProperty(KEY_GTE, maxPrice)
            priceRange.addProperty(KEY_LT, maxPrice)

            val price = JsonObject()
            price.add(KEY_PRICE, priceRange)

            val jsonArray = JsonArray()
            jsonArray.add(price)

            filters.add(KEY_OR, jsonArray)
        }

        return filters
    }

    fun deepCopy(): ProductListingRequest {
        val json = Gson().toJson(this)
        return Gson().fromJson(json, ProductListingRequest::class.java)
    }
}