package com.hexagram.febys.ui.screens.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.response.OldProduct
import com.hexagram.febys.repos.ICartRepo
import com.hexagram.febys.repos.IProductRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class CartViewModel @Inject constructor(
    private val cartRepo: ICartRepo,
    productRepo: IProductRepo
) : ProductViewModel(productRepo) {

    fun observeCart(): LiveData<List<CartDTO>> = cartRepo.observeCart()

    fun updateCartItem(cartDTO: CartDTO) = viewModelScope.launch {
        cartRepo.updateCartItem(cartDTO)
    }

    fun removeFromCart(cartDTO: CartDTO) = viewModelScope.launch {
        cartRepo.removeFromCart(cartDTO)
    }

    fun addToCart(oldProduct: OldProduct, variantId: Int) {
        val existingCartDTO = cartRepo.getCartItem(variantId)
        if (existingCartDTO != null) {
            existingCartDTO.quantity += 1
            updateCartItem(existingCartDTO)
        } else {
            val variant = oldProduct.productVariants.first { it.id == variantId }
            val cartDTO = CartDTO.fromVendorProductVariant(oldProduct = oldProduct, variant = variant)
            addToCart(cartDTO)
        }
    }

    private fun addToCart(cartDTO: CartDTO) = viewModelScope.launch {
        cartRepo.addCartItem(cartDTO)
    }

    fun sortListForCart(list: List<CartDTO>?): List<CartDTO>? {
        if (list == null) return list

        val sortedList = mutableListOf<CartDTO>()
        val vendorIds = list.map { it.vendorId }.toSet()

        vendorIds.forEach { vendorId ->
            val groupByVendor = list.filter { it.vendorId == vendorId }
            sortedList.addAll(groupByVendor.sortedByDescending { it.createdAt })
        }

        return sortedList
    }

    fun refreshCart() = viewModelScope.launch { cartRepo.refreshCart() }

    fun clearCart() = viewModelScope.launch(Dispatchers.IO) { cartRepo.clearCart() }
}