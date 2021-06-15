package com.android.febys.di

import androidx.room.Room
import com.android.febys.BuildConfig
import com.android.febys.FebysApp
import com.android.febys.database.FebysDatabaseImpl
import com.android.febys.database.IFebysDatabase
import com.android.febys.database.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: FebysApp): IFebysDatabase {
        val dbBuilder = Room.databaseBuilder(context, FebysDatabaseImpl::class.java, "FebysDB")
        if (BuildConfig.DEBUG) {
            dbBuilder.fallbackToDestructiveMigration()
        }

        return dbBuilder.build()
    }
}