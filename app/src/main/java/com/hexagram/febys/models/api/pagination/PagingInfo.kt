package com.hexagram.febys.models.api.pagination

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PagingInfo(
    @SerializedName("page_no")
    val pageNo: Int,
    @SerializedName("total_Pages")
    val totalPages: Int,
    @SerializedName("start_record")
    val startRecord: Int,
    @SerializedName("end_record")
    val endRecord: Int,
    @SerializedName("total_rows")
    val totalRows: Int
) : Parcelable