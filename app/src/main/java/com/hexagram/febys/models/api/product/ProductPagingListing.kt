package com.hexagram.febys.models.api.product

import android.os.Parcelable
import com.hexagram.febys.models.api.pagination.PagingInfo
import kotlinx.parcelize.Parcelize

@Parcelize
class ProductPagingListing(
    val products: List<Product>,
    val pagingInfo: PagingInfo,
    val totalRows: Int
) : Parcelable