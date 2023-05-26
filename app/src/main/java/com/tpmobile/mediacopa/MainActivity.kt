package com.tpmobile.mediacopa

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.tpmobile.mediacopa.ui.screens.*
import com.tpmobile.mediacopa.ui.theme.MediaCopaTPTheme



class MainActivity : ComponentActivity() {
    private lateinit var navController: NavController
    var placesClient: PlacesClient? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaCopaTPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()

                    Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY);
                    placesClient = Places.createClient(applicationContext); // provide your application context here

                    BottomMenu(placesClient ?: null)
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
//@Preview(showBackground = true)
@Composable
fun BottomMenu(placesClient: PlacesClient?) {
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
                    IconButton(onClick = { navController.navigate("Direcciones") }) {
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
                    DireccionesScreen(navController , lugar , placesClient)
                }
            }
            composable("Historial") { HistorialScreen(navController) }
            composable("Lugares") { LugaresScreen(navController) }
            composable("Mapa") { MapaScreen(navController) }
        }
    }

}