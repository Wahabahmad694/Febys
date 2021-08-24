package com.hexagram.febys.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.hexagram.febys.models.db.CartDTO

@Dao
interface CartDao : BaseDao<CartDTO> {
    @Query("Delete From CartDTO")
    fun clear()

    @Query("Select Sum(quantity) from CartDTO")
    fun observeCartCount(): LiveData<Int?>
}