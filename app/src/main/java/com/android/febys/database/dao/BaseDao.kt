package com.android.febys.database.dao

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<T>)

    @Update
    suspend fun update(obj: T)

    @Update
    suspend fun update(list: List<T>)

    @Delete
    suspend fun delete(obj: T)

    @Delete
    suspend fun delete(list: List<T>)
}