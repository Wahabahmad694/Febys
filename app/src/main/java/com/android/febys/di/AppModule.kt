package com.android.febys.di

import android.content.Context
import com.android.febys.FebysApp
import com.android.febys.prefs.IPrefManger
import com.android.febys.prefs.PrefManagerImpl
import com.android.febys.utils.PREF_FILE
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

    @Singleton
    @Provides
    fun providePrefManger(@ApplicationContext context: Context): IPrefManger {
        val sharedPreferences = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        return PrefManagerImpl(sharedPreferences)
    }
}
