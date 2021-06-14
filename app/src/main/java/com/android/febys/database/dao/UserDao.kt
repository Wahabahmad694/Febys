package com.android.febys.database.dao

import androidx.room.Dao
import com.android.febys.dto.UserDTO

@Dao
interface UserDao : BaseDao<UserDTO> {
}