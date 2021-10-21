package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.pagination.PagingInfo

data class ResponseAllCategories(
    @SerializedName("total_rows")
    val totalRows: Int,
    @SerializedName("categories")
    val categories: List<Category>,
    @SerializedName("pagination_information")
    val paginationInformation: PagingInfo
)