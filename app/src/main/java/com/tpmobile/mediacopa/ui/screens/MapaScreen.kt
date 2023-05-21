package com.tpmobile.mediacopa.ui.screens


import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.tpmobile.mediacopa.models.Address


//@Preview(showBackground = true)
@Composable
fun MapaScreen(navController: NavController) {
    //TODO: markers con direcciones
    GoogleMap(modifier = Modifier.fillMaxSize())
}
