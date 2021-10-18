package com.hexagram.febys.network.response

import com.google.gson.annotations.SerializedName

data class ResponseProductListing(
    @SerializedName("total_rows")
    val totalRows: Int,
    @SerializedName("products")
    val oldProducts: List<OldProduct>,
    @SerializedName("pagination_info")
    val paginationInformation: PaginationInformation
)
