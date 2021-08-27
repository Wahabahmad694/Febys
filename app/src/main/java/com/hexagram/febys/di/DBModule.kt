package com.hexagram.febys.di

import androidx.room.Room
import com.hexagram.febys.FebysApp
import com.hexagram.febys.db.FebysDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun provideDB(context: FebysApp) = Room
        .databaseBuilder(context, FebysDB::class.java, "FebysDB")
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()

    @Provides
    @Singleton
    fun provideCartDao(db: FebysDB) = db.provideCartDao()
}