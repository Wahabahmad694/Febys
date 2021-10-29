package com.hexagram.febys.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hexagram.febys.models.db.CartDTO
import com.hexagram.febys.network.requests.VariantAndQuantityCart

@Dao
interface CartDao : BaseDao<CartDTO> {
    @Query("Delete From CartDTO")
    fun clear()

    @Query("Select Sum(quantity) from CartDTO")
    fun observeCartCount(): LiveData<Int?>

    @Query("Select * From CartDTO Order By createdAt DESC")
    fun observeCart(): LiveData<List<CartDTO>>

    @Query("Select * From CartDTO Where skuId = :skuId")
    fun getCartItem(skuId: String): CartDTO?

    @Query("Select * From CartDTO Order By createdAt DESC")
    fun getCart(): List<CartDTO>

    @Query("Select skuId, quantity From CartDTO Order By createdAt DESC")
    fun getCartForPush(): List<VariantAndQuantityCart>
}