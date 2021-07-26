package com.hexagram.febys.ui.screens.wishlist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.network.response.ResponseProductListing
import com.hexagram.febys.repos.IProductListingRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val productListingRepo: IProductListingRepo
) : ProductViewModel(productListingRepo) {

    private var wishlistProductListing: Flow<PagingData<Product>>? = null

    fun fetchWishList(
        onProductListingResponse: ((ResponseProductListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        if (wishlistProductListing == null) {
            wishlistProductListing =
                productListingRepo.fetchWishList(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return wishlistProductListing!!
    }
}