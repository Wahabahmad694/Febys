package com.hexagram.febys.repos

import androidx.lifecycle.LiveData
import com.hexagram.febys.models.db.CartDTO

interface ICartRepo {
    fun observeCart(): LiveData<List<CartDTO>>

    suspend fun updateCartItem(cartDTO: CartDTO)

    suspend fun removeFromCart(cartDTO: CartDTO)

    fun getCartItem(skuId: String): CartDTO?

    suspend fun addCartItem(cartDTO: CartDTO)

    fun clearCart()

    suspend fun pullAndPushCart()

    suspend fun refreshCart()
}