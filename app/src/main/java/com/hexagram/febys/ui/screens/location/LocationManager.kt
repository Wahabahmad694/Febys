package com.hexagram.febys.ui.screens.location

import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.hexagram.febys.utils.PermissionsUtils
import javax.inject.Inject

class LocationManager @Inject constructor(
    val context: Application,
    val locationRequest: LocationRequest,
    val fusedLocationProviderClient: FusedLocationProviderClient
) : ILocationManager {


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999

    }

    private var isGettingLocation = false
    private var locationType: ILocationManager.LocationType? = null
    private var lastLocation: Location? = null
    private var locationFrequency: ILocationManager.LocationFrequency =
        ILocationManager.LocationFrequency.FAST

    private var onGetLocation: ((
        currentLocation: Location?,
        lastKnownLocation: Location?,
        allLocations: List<Location>?
    ) -> Unit)? = null


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            when (locationType) {
                ILocationManager.LocationType.ONETIME -> {
                    stopLocationUpdates()
                }
                ILocationManager.LocationType.UNLIMITED -> {
                    isGettingLocation = true
                }
            }

            if (lastLocation == null) {
                lastLocation =
                    locationResult.locations.firstOrNull() ?: locationResult.lastLocation
            }
            lastLocation = locationResult.locations.firstOrNull()

            onGetLocation?.invoke(
                locationResult.locations.firstOrNull(),
                locationResult.lastLocation,
                locationResult.locations
            )
        }
    }


    override fun startLocation(
        locationType: ILocationManager.LocationType,
        locationFrequency: ILocationManager.LocationFrequency,
        onGetLocation: locationHOF
    ) {

        this.locationType = locationType
        this.onGetLocation = onGetLocation
        this.locationFrequency = locationFrequency
        startLocationUpdates(locationFrequencey = locationFrequency)

    }

    override fun startLocationWithPermissionCheck(
        activity: AppCompatActivity,
        locationType: ILocationManager.LocationType,
        locationFrequency: ILocationManager.LocationFrequency,
        onGetLocation: locationHOF
    ) {
        when {
            PermissionsUtils.isAccessFineLocationGranted(context) -> {
                when {
                    PermissionsUtils.isLocationEnabled(context) -> {
                        this.locationType = locationType
                        this.onGetLocation = onGetLocation
                        this.locationFrequency = locationFrequency
                        startLocationUpdates(this.locationFrequency)
                    }
                    else -> {
                        PermissionsUtils.showGPSNotEnabledDialog(context)
                    }
                }
            }
            else -> {
                PermissionsUtils.requestAccessFineLocationPermission(
                    activity,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }

    }


    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(locationFrequencey: ILocationManager.LocationFrequency) {
        isGettingLocation = true
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest.setInterval(locationFrequencey.frequency)
                .setFastestInterval(locationFrequencey.frequency),
            locationCallback,
            Looper.getMainLooper()
        )
    }


    override fun stopLocationUpdates() {
        if (isGettingLocation) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            isGettingLocation = false

        }
    }

    override fun onRequestPermissionsResult(
        view: View,
        requestCode: Int,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionsUtils.isLocationEnabled(context) -> {
                            startLocationUpdates(locationFrequencey = locationFrequency)
                        }
                        else -> {
                            PermissionsUtils.showGPSNotEnabledDialog(context)
                        }
                    }
                } else {
                    /*  UIUtils.showSnack(
                          view,
                          message = context.getString(R.string.location_required_message)
                      )*/

                }
            }
        }
    }

    override fun getLastSavedLocation() = lastLocation


}