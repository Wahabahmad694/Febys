package com.hexagram.febys.paginations

import com.hexagram.febys.network.response.ResponseProductListing

abstract class ProductListingPagingSource<Key : Any, Value : Any>(
    val onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
) : BasePagingSource<Key, Value>()