package com.hexagram.febys.ui.screens.location

import android.location.Address
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.hexagram.febys.base.BaseViewModel
import com.hexagram.febys.models.api.location.LocationModel
import com.hexagram.febys.models.api.location.LocationSuggestion
import com.hexagram.febys.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class LocationViewModel @Inject constructor(
    private var placesClient: PlacesClient,
    private val locationUtils: LocationUtils,
    private var locationManager: LocationManager,


    ) : BaseViewModel() {

    private var fetchAddressJob: Job? = null

    private val token = AutocompleteSessionToken.newInstance()

    var pickupLocationView: MutableLiveData<LocationSuggestion?> = MutableLiveData(
        null
    )
        private set


    fun getLocationPredictions(query: String): Flow<List<AutocompletePrediction>> {
        return flow {


            val request =
                FindAutocompletePredictionsRequest.builder()
//                    .setTypeFilter(TypeFilter.ADDRESS)
                    .setSessionToken(token)
                    .setQuery(query)
                    .build()

            val predictionsResponse =
                suspendCancellableCoroutine<FindAutocompletePredictionsResponse> {
                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { p0 ->
                            it.resume(
                                p0!!
                            )
                        }.addOnFailureListener { exception: Exception? ->

                        }
                }

            emit(predictionsResponse.autocompletePredictions)

        }
    }

    fun getPlaceFromSuggestions(placeId: String) {

        fetchAddressJob?.cancel()
        fetchAddressJob = viewModelScope.launch {
            val place = locationUtils.getPlace(placeId = placeId, placesClient)
            getAddressFromLatLng(place?.latLng?.latitude ?: 0.0, place?.latLng?.longitude ?: 0.0)

        }

    }

    fun getAddressFromLatLng(latitude: Double, longitude: Double) {

        if (latitude == 0.0 || longitude == 0.0)
            return
        fetchAddressJob?.cancel()
        fetchAddressJob = viewModelScope.launch {
            val rawAddress = locationUtils.getAddress(latitude, longitude)
            val actualAddress = locationUtils.createAddressString(rawAddress)
            withContext(Dispatchers.Main) {
                updatePlace(
                    lat = latitude,
                    lng = longitude,
                    name = actualAddress,
                    address = rawAddress
                )
            }
        }
    }

    private fun updatePlace(
        name: String,
        lat: Double,
        lng: Double,
        address: Address?
    ) {

        pickupLocationView.postValue(
            LocationSuggestion(
                lat = lat,
                lng = lng,
                name = name,
                address = LocationModel(
                    address?.locality,
                    address?.adminArea,
                    country = address?.countryName,
                    postalCode = address?.postalCode
                )
            )
        )

    }

    fun hasAddress(addressFromUI: String): Boolean {

        return pickupLocationView.value?.name == addressFromUI
    }

    fun getCurrentLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            locationManager.startLocation(
                ILocationManager.LocationType.ONETIME,
                ILocationManager.LocationFrequency.FASTEST,
                onGetLocation = { currentLocation: Location?,
                                  lastKnownLocation: Location?,
                                  allLocations: List<Location>? ->

                    getAddressFromLatLng(
                        currentLocation?.latitude ?: 0.0,
                        currentLocation?.longitude ?: 0.0
                    )
                }
            )
        }

    }

}