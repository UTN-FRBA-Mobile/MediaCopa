package com.tpmobile.mediacopa.models

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place.Type

object PuntoMedio {// singleton
    var streetAddress: String? = null;
    var latLong: LatLng? = null;
    var type: String? = null ;

}
