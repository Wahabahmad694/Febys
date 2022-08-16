package com.hexagram.febys.di


import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.hexagram.febys.utils.LocationUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlacesModule {


    @Singleton
    @Provides
    fun providePlacesClient(@ApplicationContext app: Context): PlacesClient {

        return Places.createClient(app)

    }

    @Singleton
    @Provides
    fun provideLocationUtils(@ApplicationContext app: Context): LocationUtils {

        return LocationUtils(app)

    }


}