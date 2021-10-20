package com.hexagram.febys.ui.screens.wishlist

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.hexagram.febys.models.api.product.Product
import com.hexagram.febys.models.api.product.ProductPagingListing
import com.hexagram.febys.repos.IProductListingRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val productListingRepo: IProductListingRepo
) : ProductViewModel(productListingRepo) {

    private var wishlistOldProductListing: Flow<PagingData<Product>>? = null
    private var currentFav = mutableSetOf<String>()

    fun fetchWishList(
        onProductListingResponse: ((ProductPagingListing) -> Unit)? = null
    ): Flow<PagingData<Product>> {
        val latestFav = getFav()
        if (wishlistOldProductListing == null || latestFav != currentFav) {
            currentFav = latestFav

            wishlistOldProductListing =
                productListingRepo.fetchWishList(
                    viewModelScope, onProductListingResponse = onProductListingResponse
                )
        }

        return wishlistOldProductListing!!
    }
}