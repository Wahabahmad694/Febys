package com.android.febys.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android.febys.dto.User

@Database(entities = [User::class], version = 1)
abstract class FebysDatabaseImpl : RoomDatabase(), IFebysDatabase