package com.hexagram.febys.repos

import androidx.lifecycle.LiveData
import com.hexagram.febys.dataSource.ICartDataSource
import com.hexagram.febys.models.db.CartDTO
import javax.inject.Inject

class CartRepoImpl @Inject constructor(
    private val cartDataSource: ICartDataSource
) : ICartRepo {

    override fun observeCart(): LiveData<List<CartDTO>> = cartDataSource.observeCart()

    override fun updateCartItem(cartDTO: CartDTO) = cartDataSource.updateCartItem(cartDTO)

    override fun removeFromCart(cartDTO: CartDTO) = cartDataSource.removeFromCart(cartDTO)

    override fun getCartItem(variantId: Int) = cartDataSource.getCartItem(variantId)

    override fun addCartItem(cartDTO: CartDTO) = cartDataSource.addCartItem(cartDTO)
}