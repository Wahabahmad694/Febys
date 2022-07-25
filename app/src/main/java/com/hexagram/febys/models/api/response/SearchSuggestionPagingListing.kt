package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.pagination.PagingInfo
import com.hexagram.febys.models.api.product.Product
import kotlinx.parcelize.Parcelize

@Parcelize
class SearchSuggestionPagingListing(
    val search: List<Product>,
    @SerializedName("pagingInfo", alternate = ["pagination_info"])
    val pagingInfo: PagingInfo,
    @SerializedName("total_rows")
    val totalRows: Int
) : Parcelable