package com.android.febys.di

import android.content.Context
import com.android.febys.FebysApp
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideFebysApp(@ApplicationContext context: Context): FebysApp = context as FebysApp

}