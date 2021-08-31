package com.hexagram.febys.ui.screens.cart

import androidx.lifecycle.LiveData
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.response.Product
import com.hexagram.febys.repos.ICartRepo
import com.hexagram.febys.repos.IProductRepo
import com.hexagram.febys.ui.screens.product.ProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepo: ICartRepo,
    productRepo: IProductRepo
) : ProductViewModel(productRepo) {

    fun observeCart(): LiveData<List<CartDTO>> = cartRepo.observeCart()

    fun updateCartItem(cartDTO: CartDTO) = cartRepo.updateCartItem(cartDTO)

    fun removeFromCart(cartDTO: CartDTO) = cartRepo.removeFromCart(cartDTO)

    fun addToCart(product: Product, variantId: Int) {
        val existingCartDTO = cartRepo.getCartItem(variantId)
        if (existingCartDTO != null) {
            existingCartDTO.quantity += 1
            updateCartItem(existingCartDTO)
        } else {
            val variant = product.productVariants.first { it.id == variantId }
            val cartDTO = CartDTO.fromVendorProductVariant(product = product, variant = variant)
            addToCart(cartDTO)
        }
    }

    private fun addToCart(cartDTO: CartDTO) = cartRepo.addCartItem(cartDTO)

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
}