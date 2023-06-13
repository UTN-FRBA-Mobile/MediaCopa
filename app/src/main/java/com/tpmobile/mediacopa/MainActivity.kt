package com.tpmobile.mediacopa

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.tpmobile.mediacopa.ui.screens.*
import com.tpmobile.mediacopa.ui.screens.AppContext.sharedPreferences
import com.tpmobile.mediacopa.ui.theme.MediaCopaTPTheme
import com.tpmobile.mediacopa.MapState


class MainActivity : ComponentActivity() {
    private lateinit var navController: NavController
    var placesClient: PlacesClient? = null;

    // Para tener la ubicacion del dispositivo
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // Pedimos permiso para ver su ubicacion
        if (::fusedLocationProviderClient.isInitialized) {
            askPermissions()}
        setContent {
            MediaCopaTPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                   // AppContext.context = LocalContext.current
                    AppContext.context = applicationContext
                    sharedPreferences = MySharedPreferences(AppContext.context)
                    val navController = rememberNavController()

                    Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY);
                    placesClient = Places.createClient(applicationContext); // provide your application context here

                    BottomMenu(placesClient ?: null , viewModel, fusedLocationProviderClient)
                }
            }
        }
    }

    // region Permiso de ubicacion
    fun askPermissions()= when {
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            viewModel.getDeviceLocation(fusedLocationProviderClient)
        }
        else -> {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    // si bien el codigo todavia no hace nada con la ubicacion, queda para cuando querramos usarlo
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
fun BottomMenu(placesClient: PlacesClient?, viewModel : MapViewModel, fusedLocationProviderClient: FusedLocationProviderClient) {
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
                val lugar= it.arguments?.getString("lugar");
                if (lugar != null) {
                    DireccionesScreen(navController , lugar , placesClient, viewModel, fusedLocationProviderClient )
                }
            }
            composable("Historial") { HistorialScreen(navController) }
            composable("Lugares") { LugaresScreen(navController) }
            //composable("Mapa") { MapaScreen(navController) }
            composable("Mapa") { MapViewModel().MapScreen(navController) }
        }
    }
}