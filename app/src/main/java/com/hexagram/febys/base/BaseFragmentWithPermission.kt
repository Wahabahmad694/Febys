package com.hexagram.febys.base

import android.Manifest
import android.content.pm.PackageManager
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import com.hexagram.febys.R
import com.hexagram.febys.utils.showToast

abstract class BaseFragmentWithPermission:BaseFragment() {
    private var permissionArrays =
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_MEDIA_LOCATION
        )

    val PERMISSION_CODE = 201

    private val TAG: String = "TAG_PERMISSION"

    fun enableRunTimePermission(permissions: Array<String>): Boolean {
        permissionArrays = permissions

        return if (!hasPermissions(*permissions)) {
            ActivityCompat.requestPermissions(requireActivity(), permissions, PERMISSION_CODE);
            false
        } else
            true
    }

    private fun hasPermissions(vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(requireActivity(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String?>,
        @NonNull grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                var permissionGranted = true

                permissionGranted = grantResults.filter { value ->
                    value == -1
                }.isEmpty()

                if (permissionGranted) {
                    showToast(getString(R.string.permission_granted))
                } else {
                    showToast(getString(R.string.permission_denied))
                }
            }
            else ->
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}