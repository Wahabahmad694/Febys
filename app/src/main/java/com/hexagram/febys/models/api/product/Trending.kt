package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.hexagram.febys.models.api.pagination.PagingInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class Trending(
    @SerializedName("top_performers")
    val topPerformers: List<TopPerformer>,
    val pagingInfo: PagingInfo,
    val totalRows: Int
) : Parcelable
