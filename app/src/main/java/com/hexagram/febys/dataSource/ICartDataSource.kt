package com.hexagram.febys.dataSource

import androidx.lifecycle.LiveData
import com.hexagram.febys.models.api.cart.Cart
import com.hexagram.febys.models.api.cart.CartResponse
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.requests.SkuIdAndQuantity

interface ICartDataSource {
    suspend fun insertCart(cartDTO: CartDTO)

    suspend fun insertCart(cart: CartResponse)

    fun clear()

    fun observeCartCount(): LiveData<Int?>

    fun observeCart(): LiveData<List<CartDTO>>

    fun updateCartItem(cartDTO: CartDTO)

    fun removeFromCart(cartDTO: CartDTO)

    fun getCartItem(skuId: String): CartDTO?

    fun addCartItem(cartDTO: CartDTO)

    fun mergeCart(cart: CartResponse)

    fun updateCart(cart: Cart)

    fun updateCart(listOfCartDTO: List<CartDTO>)

    fun replaceOrAddCart(listOfCartDTO: List<CartDTO>)

    fun getCart(): List<CartDTO>

    fun getCartSkuIdsAndQuantity(): List<SkuIdAndQuantity>
}