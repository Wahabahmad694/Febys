package com.hexagram.febys.models.api.product

import com.hexagram.febys.models.api.pagination.PagingInfo
import com.hexagram.febys.models.api.pagination.PagingListing
import kotlinx.parcelize.Parcelize

@Parcelize
class ProductPagingListing(
    val products: List<Product>,
    override val pagingInfo: PagingInfo,
    override val totalRows: Int
) : PagingListing(pagingInfo, totalRows)