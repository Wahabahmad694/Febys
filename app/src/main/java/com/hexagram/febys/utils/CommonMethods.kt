package com.hexagram.febys.utils

import android.Manifest
import java.util.*

class CommonMethods {
    val currentDateTimeInUTC: Date
        get() {
            val calendar = Calendar.getInstance(
                TimeZone.getTimeZone("GMT"),
                Locale.getDefault()
            )
            return calendar.time
        }

    companion object {

        fun getStorageAndCameraPermissions(): Array<String> {
            return arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        }


    }
}