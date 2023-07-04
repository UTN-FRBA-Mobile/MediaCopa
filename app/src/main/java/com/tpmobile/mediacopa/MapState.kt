package com.tpmobile.mediacopa

import android.location.Location
import com.tpmobile.mediacopa.model.AddressesItem
import com.tpmobile.mediacopa.model.Meeting

object MapState { // Singleton
    var lastKnownLocation: AddressesItem? = null
    var midpointAddress: Meeting? = null
    var otherAddresses: List<AddressesItem>? = null
}