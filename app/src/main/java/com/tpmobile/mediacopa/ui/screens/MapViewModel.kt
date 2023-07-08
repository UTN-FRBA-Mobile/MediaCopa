package com.tpmobile.mediacopa.ui.screens


import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.tpmobile.mediacopa.MapState
import com.tpmobile.mediacopa.model.AddressesItem


//import com.tpmobile.mediacopa.ui.screens.AppContext.context


class MapViewModel(): ViewModel() {
    private var DEFAULT_ZOOM = 16F
    private var defaultLocation = LatLng(-34.5986174, -58.4201076)
    private var map: GoogleMap? = null

    fun shareInfo(context: Context) {
        var midpointAddress = MapState.midpointAddress;

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
        }
        var locationStringW = translateToDegrees(latitude = midpointAddress?.lat as Double, longitude = midpointAddress?.lon as Double)

        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Media Copa")
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Podemos encontrarnos aqui: https://www.google.com/maps/place/${locationStringW}/")



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
        var context = LocalContext.current;

        var location = defaultLocation
        if (MapState.midpointAddress != null) {
            location = LatLng(
                MapState.midpointAddress!!.lat as Double,
                MapState.midpointAddress!!.lon as Double
            )
        }

        GoogleMap(
            modifier = Modifier
                .padding(bottom = 50.dp)
                .fillMaxSize(),
            cameraPositionState = CameraPositionState(CameraPosition.fromLatLngZoom(location, DEFAULT_ZOOM)),
        ) {
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
                        icon = BitmapDescriptorFactory.defaultMarker(246.0F)
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

    private fun translateToDegrees(latitude: Double, longitude: Double): String? {
        val builder = StringBuilder()

        val latitudeDegrees = Location.convert(Math.abs(latitude), Location.FORMAT_SECONDS)
        val latitudeSplit = latitudeDegrees.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        builder.append(latitudeSplit[0])
        builder.append("°")
        builder.append(latitudeSplit[1])
        builder.append("'")
        builder.append(latitudeSplit[2])
        builder.append("\"")
        if (latitude < 0) {
            builder.append("S")
        } else {
            builder.append("N")
        }
        builder.append("+")
        val longitudeDegrees = Location.convert(Math.abs(longitude), Location.FORMAT_SECONDS)
        val longitudeSplit = longitudeDegrees.split(":".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        builder.append(longitudeSplit[0])
        builder.append("°")
        builder.append(longitudeSplit[1])
        builder.append("'")
        builder.append(longitudeSplit[2])
        builder.append("\"")
        if (longitude < 0) {
            builder.append("W")
        } else {
            builder.append("E")
        }
        return builder.toString()
    }


    @SuppressLint("MissingPermission")
    fun getDeviceLocation(fusedLocationProviderClient: FusedLocationProviderClient){
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnSuccessListener { task ->
                if (task != null) {
                    var address = AddressesItem(
                        streetAddress = "Mi ubicacion", //por favor no cambiar porque hay codigo que depende de este nombre
                        lat = task?.latitude,
                        lon = task?.longitude
                    );
                    if (address.lat == null) {
                        Log.d("TAG", "Fallo la obtencion de la ubicacion, usamos una por defecto")
                        address = AddressesItem(
                            streetAddress = "Mi ubicacion", //por favor no cambiar porque hay codigo que depende de este nombre
                            lat = -34.5986444,
                            lon = -58.4415858
                        );
                        Log.d(TAG, "Current location is null. Using defaults.")
                    }
                    MapState.lastKnownLocation = address
                }
            }
        } catch (e: SecurityException) {
            Log.e("error", e.toString())
        }
    }
}