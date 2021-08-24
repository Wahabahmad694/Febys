package com.hexagram.febys.dataSource

import androidx.lifecycle.LiveData
import com.hexagram.febys.db.dao.CartDao
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.domain.util.CartMapper
import com.hexagram.febys.network.response.Cart
import javax.inject.Inject

class CartDataSourceImpl @Inject constructor(
    private val cartDao: CartDao,
    private val cartMapper: CartMapper
) : ICartDataSource {
    override suspend fun insertCart(cartDTO: CartDTO) {
        cartDao.insert(cartDTO)
    }

    override suspend fun insertCart(cart: Cart) {
        val listOfCartDTO = cartMapper.mapFromDomainModel(cart)
        cartDao.insert(listOfCartDTO)
    }

    override fun clear() {
        cartDao.clear()
    }

    override fun observeCartCount(): LiveData<Int?> {
        return cartDao.observeCartCount()
    }
}