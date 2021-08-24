package com.hexagram.febys.dataSource

import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.response.Cart

interface ICartDataSource {
    suspend fun insertCart(cartDTO: CartDTO)

    suspend fun insertCart(cart: Cart)

    fun clear()
}