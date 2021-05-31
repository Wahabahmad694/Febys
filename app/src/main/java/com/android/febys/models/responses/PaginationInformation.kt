package com.android.febys.models.responses

import com.google.gson.annotations.SerializedName

data class PaginationInformation(
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
)
