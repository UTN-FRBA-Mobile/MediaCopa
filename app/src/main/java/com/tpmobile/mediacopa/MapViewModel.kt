package com.tpmobile.mediacopa

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap


class MapViewModel(): ViewModel(), OnMapReadyCallback {
    private var DEFAULT_ZOOM = 15
    private var defaultLocation = LatLng(-34.5986174, -58.4201076)
    private var map: GoogleMap? = null

    @Composable
    fun MapViewModel(navController: NavController) {
        var myLocation = defaultLocation;

        if(state.value.lastKnownLocation != null) {
            myLocation = LatLng(state.value.lastKnownLocation!!.latitude,  state.value.lastKnownLocation!!.longitude);
            //TODO: la idea es que aca este en el punto medio, no en mi ubi, pero funciona
        }

        GoogleMap(modifier = Modifier.fillMaxSize(),
            cameraPositionState = CameraPositionState(CameraPosition(myLocation, 50F, 0F, 0F))
        )
    }

    val state: MutableState<MapState> = mutableStateOf(
        MapState(
            lastKnownLocation = null
        )
    )

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(
        fusedLocationProviderClient: FusedLocationProviderClient
    ) {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    state.value = state.value.copy(
                        lastKnownLocation = task.result,
                    )
                    if (state.value.lastKnownLocation != null) {
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                            LatLng(state.value.lastKnownLocation!!.latitude,
                                state.value.lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                    }
                    else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            // Show error or something
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map;
        Log.i("MAP","El mapa ya cargo!!")
    }
}