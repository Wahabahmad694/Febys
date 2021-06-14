package com.android.febys.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.android.febys.dto.UserDTO

@Dao
interface UserDao : BaseDao<UserDTO> {
    @Query("Select * from UserDTO Limit 1")
    fun getUser(): UserDTO
}