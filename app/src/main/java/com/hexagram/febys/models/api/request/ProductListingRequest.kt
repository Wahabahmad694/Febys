package com.hexagram.febys.models.api.request

data class ProductListingRequest constructor(
    val searchStr: String = "",
    val filters: ProductFilters = ProductFilters()
)