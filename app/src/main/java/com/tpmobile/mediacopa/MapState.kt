package com.tpmobile.mediacopa

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.tpmobile.mediacopa.models.PuntoMedio

object MapState { // Singleton
    var lastKnownLocation: Location? = null
    var midpointAddress: PuntoMedio? = null
}