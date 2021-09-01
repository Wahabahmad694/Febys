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

    override fun observeCart(): LiveData<List<CartDTO>> {
        return cartDao.observeCart()
    }

    override fun updateCartItem(cartDTO: CartDTO) {
        cartDao.update(cartDTO)
    }

    override fun removeFromCart(cartDTO: CartDTO) {
        cartDao.delete(cartDTO)
    }

    override fun getCartItem(variantId: Int): CartDTO? {
        return cartDao.getCartItem(variantId)
    }

    override fun addCartItem(cartDTO: CartDTO) {
        cartDao.insert(cartDTO)
    }

    override fun mergeCart(cart: Cart) {
        val listOfCartDTO = cartMapper.mapFromDomainModel(cart)
        cartDao.insertAndIgnoreIfAlreadyExist(listOfCartDTO)
    }

    override fun updateCart(cart: Cart) {
        val listOfCartDTO = cartMapper.mapFromDomainModel(cart)
        cartDao.update(listOfCartDTO)
    }

    override fun getCart() = cartDao.getCart()

    override fun getCartForPush() = cartDao.getCartForPush()
}