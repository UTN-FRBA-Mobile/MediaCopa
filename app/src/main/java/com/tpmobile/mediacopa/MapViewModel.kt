package com.tpmobile.mediacopa

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap


import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.tpmobile.mediacopa.models.Address


class MapViewModel(): ViewModel(), OnMapReadyCallback {
    private var DEFAULT_ZOOM = 15
    private var defaultLocation = LatLng(-34.5986174, -58.4201076)
    private var map: GoogleMap? = null

    fun shareInfo() {
        var midpointAddress = MapState.midpointAddress;

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Podemos encontrarnos en ${midpointAddress?.streetAddress.toString()}")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        context.startActivity(shareIntent) // todo no se porque falla
    }

    @Composable
    fun MapScreen(navController: NavController, type: String, lat: Float, lon: Float, streetAddress: String) {
        //TODO me tengo que traer el punto emdio y marcarlo en el mapa
        Log.e("tipo", type)
        Log.e("lat", lat.toString())
        Log.e("long", lon.toString())

        GoogleMap(
            modifier = Modifier.fillMaxSize() ,
            cameraPositionState = CameraPositionState(CameraPosition(LatLng(lat.toDouble(), lon.toDouble()), DEFAULT_ZOOM.toFloat(), 0F, 0F))
        )

        MapState.midpointAddress = Address(streetAddress,  LatLng(lat.toDouble(), lon.toDouble()), null, 0)

        FloatingActionButton(
            onClick = { this.shareInfo() },
            modifier = Modifier.padding(15.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Compartir"
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun getDeviceLocation(fusedLocationProviderClient: FusedLocationProviderClient){
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    MapState.lastKnownLocation = task.result
                    Log.e("MAPAAAAAAAAAAAAAAA",(MapState.lastKnownLocation).toString());//todo borrar
                    if (MapState.lastKnownLocation != null) {
                        Log.e("MAPa",(map == null).toString())
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                            LatLng(MapState.lastKnownLocation!!.latitude,
                                MapState.lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
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
        Log.e("MAP","El mapa ya cargo!!")
    }
}