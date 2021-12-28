package com.hexagram.febys.models.api.request

import android.os.Parcelable
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderListingRequest constructor(
    var filters: List<String>? = null,
    var hasReviewed: Boolean = false
) : Parcelable {
    fun createRequest(): JsonObject {
        val req = JsonObject()

        if (hasReviewed) {
            val filter = JsonObject()
            filter.addProperty("vendor_products.hasReviewed", true)
            req.add("filters", filter)
        } else if (!filters.isNullOrEmpty()) {
            val filterArray = JsonArray()
            filters?.forEach { filterArray.add(it) }
            val multiFilters = JsonObject()
            multiFilters.add("\$in", filterArray)
            val filter = JsonObject()
            filter.add("vendor_products.status", multiFilters)
            req.add("filters", filter)
        }

        return req
    }
}