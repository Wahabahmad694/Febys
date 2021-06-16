package com.android.febys.di

import android.content.Context
import com.android.febys.FebysApp
import com.android.febys.prefs.IPrefManger
import com.android.febys.prefs.PrefManagerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {
    companion object{
        @Singleton
        @Provides
        fun provideFebysApp(@ApplicationContext context: Context): FebysApp = context as FebysApp
    }

    @Singleton
    @Binds
    abstract fun bindPrefManger(pref: PrefManagerImpl): IPrefManger
}
