package com.hexagram.febys.di

import android.app.Application
import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.hexagram.febys.ui.screens.location.ILocationManager
import com.hexagram.febys.ui.screens.location.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Module responsible Location updates.
 */
@Module
@InstallIn(SingletonComponent::class)
class LocationModule {

    @Provides
    @Singleton
    fun provideLocationRequest(): LocationRequest {
        return LocationRequest.create().setInterval(NORMAL_INTERVAL)
            .setFastestInterval(FASTEST_INTERVAL)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    @Provides
    @Singleton
    fun provideLocationProviderClient(@ApplicationContext app: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(app)
    }


    @Provides
    @Singleton
    fun provideLocationManager(
        app: Application,
        locationRequest: LocationRequest,
        fusedLocationProviderClient: FusedLocationProviderClient
    ): ILocationManager {
        return LocationManager(app, locationRequest, fusedLocationProviderClient)
    }

    companion object {
        private const val NORMAL_INTERVAL: Long = 2000
        private const val FASTEST_INTERVAL: Long = 2000
        private const val MAP_LOCATION_SMALLEST_DISPLACEMENT = 0
    }
}