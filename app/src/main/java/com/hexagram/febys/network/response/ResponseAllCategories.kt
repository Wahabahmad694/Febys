package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class ResponseAllCategories(
    @SerializedName("total_rows")
    val totalRows: Int,
    @SerializedName("categories")
    val categories: List<Category>,
    @SerializedName("pagination_information")
    val paginationInformation: PaginationInformation
)