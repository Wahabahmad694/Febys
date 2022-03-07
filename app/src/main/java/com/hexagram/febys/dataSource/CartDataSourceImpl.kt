package com.hexagram.febys.dataSource

import androidx.lifecycle.LiveData
import com.hexagram.febys.db.dao.CartDao
import com.hexagram.febys.models.api.cart.Cart
import com.hexagram.febys.models.api.cart.CartResponse
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.domain.util.CartMapper
import javax.inject.Inject

class CartDataSourceImpl @Inject constructor(
    private val cartDao: CartDao,
    private val cartMapper: CartMapper
) : ICartDataSource {
    override suspend fun insertCart(cartDTO: CartDTO) {
        cartDao.insert(cartDTO)
    }

    override suspend fun insertCart(cart: CartResponse) {
        val listOfCartDTO = cartMapper.mapFromDomainModel(cart.cart)
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

    override fun getCartItem(skuId: String): CartDTO? {
        return cartDao.getCartItem(skuId)
    }

    override fun addCartItem(cartDTO: CartDTO) {
        cartDao.insert(cartDTO)
    }

    override fun mergeCart(cart: CartResponse) {
        val listOfCartDTO = cartMapper.mapFromDomainModel(cart.cart)
        cartDao.insertAndIgnoreIfAlreadyExist(listOfCartDTO)
    }

    override fun updateCart(cart: Cart) {
        val listOfCartDTO = cartMapper.mapFromDomainModel(cart)
        updateCart(listOfCartDTO)
    }

    override fun updateCart(listOfCartDTO: List<CartDTO>) {
        val dbCart = cartDao.getCart()
        listOfCartDTO.forEach { cartItem ->
            val dbCartItem =
                dbCart.firstOrNull { dbCartItem -> cartItem.skuId == dbCartItem.skuId }
            if (dbCartItem != null) {
                updateCartItemIfChange(dbCartItem, cartItem)
            } else {
                addCartItem(cartItem)
            }
        }
    }

    override fun replaceOrAddCart(listOfCartDTO: List<CartDTO>) {
        val dbCart = cartDao.getCart()

        val itemsToRemove = dbCart.filter { dbCartItem ->
            listOfCartDTO.firstOrNull { dbCartItem.skuId == it.skuId } == null
        }
        cartDao.delete(itemsToRemove)

        listOfCartDTO.forEach { cartItem ->
            val dbCartItem =
                dbCart.firstOrNull { dbCartItem -> cartItem.skuId == dbCartItem.skuId }
            if (dbCartItem != null) {
                val updatedCartItem = cartItem.copy(
                    createdAt = dbCartItem.createdAt
                )
                cartDao.update(updatedCartItem)
            } else {
                addCartItem(cartItem)
            }
        }
    }

    private fun updateCartItemIfChange(dbCartItem: CartDTO, cartItem: CartDTO) {
        if (dbCartItem.hasPromotion != cartItem.hasPromotion) {
            cartDao.update(cartItem)
        }
    }

    override fun getCart() = cartDao.getCart()

    override fun getCartSkuIdsAndQuantity() = cartDao.getCartSkuIdsAndQuantity()
}