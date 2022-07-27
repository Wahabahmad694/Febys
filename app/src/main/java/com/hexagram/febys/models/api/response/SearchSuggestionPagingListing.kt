package com.hexagram.febys.models.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.pagination.PagingInfo
import com.hexagram.febys.models.api.suggestedSearch.SuggestedProduct
import kotlinx.parcelize.Parcelize

@Parcelize
class SearchSuggestionPagingListing(
    val search: List<SuggestedProduct>,
    @SerializedName("pagingInfo", alternate = ["pagination_info"])
    val pagingInfo: PagingInfo,
    @SerializedName("total_rows")
    val totalRows: Long,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val nextPage: Int,
    val previousPage: Int,
    val totalDocs: Long,
) : Parcelable