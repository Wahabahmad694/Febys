package com.android.febys.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.android.febys.dto.User

@Dao
interface UserDao : BaseDao<User> {
    @Query("Select * from User Limit 1")
    fun getUser(): User
}