package com.tpmobile.mediacopa.ui.screens


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.tpmobile.mediacopa.MapState


//import com.tpmobile.mediacopa.ui.screens.AppContext.context


class MapViewModel(): ViewModel() {
    private var DEFAULT_ZOOM = 15F
    private var defaultLocation = LatLng(-34.5986174, -58.4201076)
    private var map: GoogleMap? = null

    fun shareInfo(context: Context) {
        var midpointAddress = MapState.midpointAddress;

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
        }

        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Media Copa")
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Podemos encontrarnos en ${midpointAddress?.streetAddress.toString()}")

        val shareIntent = Intent.createChooser(sendIntent, "Share")
        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        ContextCompat.startActivity(
            context,
            shareIntent,
            null
        )
    }

    @Composable
    fun MapScreen(navController: NavController) {
        //var uiSettings by remember { mutableStateOf(MapUiSettings()) }

        var context = LocalContext.current;

        var location = if (MapState.midpointAddress != null)
            LatLng(MapState.midpointAddress!!.lat as Double, MapState.midpointAddress!!.lon as Double)
            else defaultLocation

        var cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(location, DEFAULT_ZOOM)
        }

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            //uiSettings = uiSettings
        ) {
//            Switch(
//                checked = uiSettings.zoomControlsEnabled,
//                onCheckedChange = {
//                    uiSettings = uiSettings.copy(zoomControlsEnabled = it)
//                }
//            )
//            Button(
//                onClick = {
//                    cameraPositionState.move(CameraUpdateFactory.newLatLng(location))
//                }
//            ) {
//                Icon(Icons.Filled.LocationOn, contentDescription = "Volver al punto medio" )
//            }
            Marker( // el punto marcado de un color, el resto de las addresses de otro
                position = location,
                title = "Punto Medio",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
            )

            if (MapState.otherAddresses != null) {
                MapState.otherAddresses?.forEach {
                    Marker(
                        position = LatLng(it?.lat as Double, it?.lon as Double),
                        title = it?.streetAddress as String?,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { this.shareInfo(context) },
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
                    if (MapState.lastKnownLocation != null) {
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                MapState.lastKnownLocation!!.latitude,
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
}