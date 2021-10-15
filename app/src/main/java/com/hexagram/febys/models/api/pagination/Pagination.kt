package com.hexagram.febys.models.api.pagination

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pagination(
    val listing : PagingListing
): Parcelable
