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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.tpmobile.mediacopa.ui.screens.AppContext.context


class MapViewModel(): ViewModel(), OnMapReadyCallback {
    private var DEFAULT_ZOOM = 15
    private var defaultLocation = LatLng(-34.5986174, -58.4201076)
    private var map: GoogleMap? = null

    fun shareInfo() {
        var midpointAddress = MapState.midpointAddress; // del MapState

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Podemos encontrarnos en ${midpointAddress?.streetAddress}")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }

    @Composable
    fun MapScreen(navController: NavController) {
        //TODO me tengo que traer el punto emdio y marcarlo en el mapa
        GoogleMap(modifier = Modifier.fillMaxSize())

        FloatingActionButton(
            onClick = { this.shareInfo() },
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
        Log.i("MAP","El mapa ya cargo!!")
    }
}