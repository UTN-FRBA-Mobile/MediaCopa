package com.tpmobile.mediacopa.ui.screens


import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.GoogleMap


//@Preview(showBackground = true)
@Composable
fun MapaScreen(navController: NavController) {

    GoogleMap(modifier = Modifier.fillMaxSize())
}


