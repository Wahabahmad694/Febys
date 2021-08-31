package com.hexagram.febys.dataSource

import androidx.lifecycle.LiveData
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.requests.VariantAndQuantityCart
import com.hexagram.febys.network.response.Cart

interface ICartDataSource {
    suspend fun insertCart(cartDTO: CartDTO)

    suspend fun insertCart(cart: Cart)

    fun clear()

    fun observeCartCount(): LiveData<Int?>

    fun observeCart(): LiveData<List<CartDTO>>

    fun updateCartItem(cartDTO: CartDTO)

    fun removeFromCart(cartDTO: CartDTO)

    fun getCartItem(variantId: Int): CartDTO?

    fun addCartItem(cartDTO: CartDTO)

    fun mergeCart(cart: Cart)

    fun getCart(): List<CartDTO>

    fun getCartForPush(): List<VariantAndQuantityCart>
}