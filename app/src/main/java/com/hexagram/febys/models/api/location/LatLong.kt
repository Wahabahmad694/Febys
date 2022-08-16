package com.hexagram.febys.models.api.location

import java.io.Serializable

data class LatLong(
    var lat: Double? = null,
    var lng: Double? = null,
) : Serializable