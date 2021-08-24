package com.hexagram.febys.db.dao

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: List<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(obj: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(obj: List<T>)

    @Delete
    fun delete(obj: T)

    @Delete
    fun delete(obj: List<T>)
}