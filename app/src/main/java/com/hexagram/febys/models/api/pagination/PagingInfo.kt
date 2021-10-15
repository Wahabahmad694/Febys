package com.hexagram.febys.models.api.pagination

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PagingInfo(
    val page_no: Int,
    val total_Pages: Int,
    val start_record: Int,
    val end_record: Int,
    val total_rows: Int
) : Parcelable