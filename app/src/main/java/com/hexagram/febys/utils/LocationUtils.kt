package com.hexagram.febys.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.*


class LocationUtils(val context: Context) {


    suspend fun getCountryFromLocation(
        lat: Double,
        lng: Double,
        onCountryName: ((String) -> Unit)
    ) {

        getAddress(lat, lng)?.let {
            onCountryName.invoke(it.countryName)

        }

    }

    suspend fun getFullAddressFromLocation(
        lat: Double,
        lng: Double,
        onCountryCode: ((String) -> Unit)
    ) {

        getAddress(lat, lng)?.let {
            onCountryCode.invoke(it.countryCode)

        }

    }

    suspend fun getFullAddressFromLocation(
        lat: Double,
        lng: Double
    ): String {

        return createAddressString(getAddress(lat, lng))
    }

     fun createAddressString(address: Address?): String {
        val builder = StringBuilder()

        address?.let { mAddress ->

            try {
                builder.append(mAddress.getAddressLine(0))

            } catch (e: IllegalArgumentException) {
                if (mAddress.hasLatitude() && mAddress.hasLongitude()) {
                    builder.append("${mAddress.latitude}, ${mAddress.longitude}")
                } else {
                    builder.append("no address found!")
                }
            }
        }

        address?.postalCode?.let {
            builder.append(",$it")

        }
        return builder.toString()
    }

    suspend fun getAddress(lat: Double, lng: Double): Address? {

        return try {
            withContext(Dispatchers.IO)
            {
                val gcd = Geocoder(context, Locale.getDefault())
                val addresses: List<Address> = gcd.getFromLocation(lat, lng, 1)

                addresses.firstOrNull()
            }

        } catch (e: Exception) {
            null
        }

    }


    suspend fun getPlace(placeId: String, placesClient: PlacesClient): Place? {
        val placeFields = listOf(Place.Field.LAT_LNG)

        val request = FetchPlaceRequest.newInstance(placeId, placeFields)

        return suspendCancellableCoroutine {
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->

                    it.resumeWith(Result.success(response.place))
                }.addOnFailureListener { exception: Exception ->
                    it.resumeWith(Result.failure(exception))
                }
        }


    }


}