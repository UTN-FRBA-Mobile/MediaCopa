package com.tpmobile.mediacopa.models

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place.Type

class Address(val streetAddress: String?, val latLong: LatLng?, val type: Type?, val position: Int) {

}