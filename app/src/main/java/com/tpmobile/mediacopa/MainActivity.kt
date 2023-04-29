package com.tpmobile.mediacopa

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.maps.android.compose.GoogleMap
import com.tpmobile.mediacopa.ui.screens.DireccionesScreen
import com.tpmobile.mediacopa.ui.screens.HistorialScreen
import com.tpmobile.mediacopa.ui.screens.LugaresScreen
import com.tpmobile.mediacopa.ui.theme.MediaCopaTPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaCopaTPTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GoogleMap(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview(showBackground = true)
@Composable
fun BottomMenu() {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: ... */ },) {
                Icon(Icons.Filled.LocationOn,
                    contentDescription = "Direcciones",
                    tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        bottomBar = {
            BottomAppBar {

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { /* TODO: Navegar a la página de historial */ }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Historial")
                    }
                    IconButton(onClick = { /* TODO: Navegar a la página de lugares */ }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Lugares")
                    }
                }

            }
        }
    ) {
        val navController = rememberNavController()
        NavHost(navController, startDestination = "Direcciones") {
            composable("Direcciones") { DireccionesScreen(navController) }
            composable("Historial") { HistorialScreen(navController) }
            composable("Lugares") { LugaresScreen(navController) }
        }
    }

}