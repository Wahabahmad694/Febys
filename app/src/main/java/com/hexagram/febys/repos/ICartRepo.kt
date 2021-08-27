package com.hexagram.febys.repos

import androidx.lifecycle.LiveData
import com.hexagram.febys.models.db.CartDTO

interface ICartRepo {
    fun observeCart(): LiveData<List<CartDTO>>

    fun updateCartItem(cartDTO: CartDTO)

    fun removeFromCart(cartDTO: CartDTO)

    fun getCartItem(variantId: Int): CartDTO?

    fun addCartItem(cartDTO: CartDTO)
}