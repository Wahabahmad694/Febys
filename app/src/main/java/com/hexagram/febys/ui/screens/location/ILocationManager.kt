package com.hexagram.febys.ui.screens.location

import android.location.Location
import android.view.View
import androidx.appcompat.app.AppCompatActivity

typealias locationHOF = ((
    currentLocation: Location?,
    lastKnownLocation: Location?,
    allLocations: List<Location>?
) -> Unit)

interface ILocationManager {


    enum class LocationType {
        ONETIME,
        UNLIMITED
    }

    enum class LocationFrequency(val frequency: Long) {
        SLOW(8000),
        NORMAL(6000),
        FAST(4000),
        FASTEST(2000)
    }

    fun startLocation(
        locationType: LocationType, locationFrequency: LocationFrequency, onGetLocation: locationHOF
    )

    fun startLocationWithPermissionCheck(
        activity: AppCompatActivity,
        locationType: LocationType, locationFrequency: LocationFrequency, onGetLocation: locationHOF
    )

    fun stopLocationUpdates()

    fun onRequestPermissionsResult(
        view: View,
        requestCode: Int,
        grantResults: IntArray
    )

    fun getLastSavedLocation(): Location?
}