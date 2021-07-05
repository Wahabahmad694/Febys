package com.android.febys.network.response

import com.google.gson.annotations.SerializedName

data class ResponseProductListing(
    @SerializedName("total_rows")
    val totalRows: Int,
    @SerializedName("products")
    val products: List<Product>,
    @SerializedName("pagination_information")
    val paginationInformation: PaginationInformation
)
