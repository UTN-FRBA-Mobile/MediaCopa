package com.tpmobile.mediacopa

import android.location.Location
import com.google.android.gms.maps.model.LatLng

data class MapState(
    val lastKnownLocation: Location?,
    val googleMapsLatLong: LatLng? = lastKnownLocation?.let { LatLng(it.latitude, lastKnownLocation.longitude) }
)