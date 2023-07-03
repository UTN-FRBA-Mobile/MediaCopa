package com.tpmobile.mediacopa

import android.location.Location
import com.tpmobile.mediacopa.model.AddressesItem

object MapState { // Singleton
    var lastKnownLocation: Location? = null
    var midpointAddress: AddressesItem? = null
    var otherAddresses: List<AddressesItem>? = null
}