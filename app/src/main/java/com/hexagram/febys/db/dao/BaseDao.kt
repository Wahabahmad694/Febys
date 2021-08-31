package com.hexagram.febys.db.dao

import androidx.room.*

@Dao
interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(obj: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(list: List<T>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAndIgnoreIfAlreadyExist(obj: T)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAndIgnoreIfAlreadyExist(list: List<T>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(obj: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(list: List<T>)

    @Delete
    fun delete(obj: T)

    @Delete
    fun delete(list: List<T>)
}