package com.tpmobile.mediacopa

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.tpmobile.mediacopa.model.AddressesItem
import com.tpmobile.mediacopa.model.RequestMeetings
import com.tpmobile.mediacopa.ui.screens.*
import com.tpmobile.mediacopa.ui.theme.MediaCopaTPTheme


class MainActivity : ComponentActivity() {
    var placesClient: PlacesClient? = null

    val address1 = AddressesItem(lon = -57.4086263, lat = -35.6404408)
    val address2 = AddressesItem(lon = -56.4086263, lat = -34.6404408)
    val address3 = AddressesItem(lon = -55.4086263, lat = -36.6404408)
    val listOfAddresses = listOf(address1, address2, address3)

    val requestMiddlePointBody = RequestMeetings(
        type= "CAFE",
        addresses = listOfAddresses
    )


    // Para tener la ubicacion del dispositivo
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Mapa
    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // Pedimos permiso para ver su ubicacion
        if (::fusedLocationProviderClient.isInitialized) {
            askPermissions()}
        getHistorial()
        setContent {
            MediaCopaTPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
                    placesClient =
                        Places.createClient(applicationContext) // provide your application context here

                    BottomMenu(viewModel)
                }
            }
        }
    }

    // region Permiso de ubicacion
    private fun askPermissions()= when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) -> {
            viewModel.getDeviceLocation(fusedLocationProviderClient)
        }
        else -> {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.getDeviceLocation(fusedLocationProviderClient)
            }
        }
    // endregion
}



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
//@Preview(showBackground = true)
@Composable
fun BottomMenu(viewModel : MapViewModel) {
    val navController = rememberNavController()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("Lugares") }) {
                Image(
                    painter = painterResource(R.drawable.zoom),
                    contentDescription = "Delete",)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        bottomBar = {
            BottomNavigation  {

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = {  navController.navigate("Historial") }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Historial")
                    }
                    IconButton(onClick = { navController.navigate("Direcciones/Place.Type.POINT_OF_INTEREST") }) {
                        Icon(Icons.Filled.Place, contentDescription = "Direcciones")
                    }
                }

            }
        }
    ) {

        NavHost(navController, startDestination = "Lugares") {
            composable("Direcciones/{lugar}") {
                val lugar= it.arguments?.getString("lugar")

                if (lugar != null) {
                    DireccionesViewModel().DireccionesScreen(navController , lugar, viewModel)
                }
            }
            composable("Historial") { HistorialScreen(navController) }
            composable("Lugares") { LugaresScreen(navController) }
            composable("Mapa/{type}/{lat}/{lon}/{streetAddress}") {
                val type = it.arguments?.getString("type")
                val lat = it.arguments?.getDouble("lat")
                val lon = it.arguments?.getDouble("lon")
                val streetAddress = it.arguments?.getString("streetAddress")


                if (type != null && lat != null && lon != null && streetAddress != null ) {
                    MapViewModel().MapScreen(navController, type, lat, lon, streetAddress)
                }
            }
        }
    }
}