package com.hexagram.febys.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hexagram.febys.db.converter.TypeConverter
import com.hexagram.febys.db.dao.CartDao
import com.hexagram.febys.models.db.CartDTO

@Database(entities = [CartDTO::class], version = 1, exportSchema = false)
@TypeConverters(TypeConverter::class)
abstract class FebysDB : RoomDatabase() {

    abstract fun provideCartDao(): CartDao
}