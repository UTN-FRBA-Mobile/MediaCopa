package com.tpmobile.mediacopa.ui.screens

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import android.icu.text.CaseMap.Title
import android.text.Layout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.tpmobile.mediacopa.R


//@Preview(showBackground = true)
@Composable
fun DireccionesScreen(navController: NavController) { // hay que comentar los parametros para poder usar el preview

    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "Inserte las direcciones",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 20.dp , top= 20.dp),
            )

        var contador by remember { mutableStateOf(0) }

        repeat(2) {
            AutoUpdatingTextField();
        }
        repeat(contador) {
           Row() {
                AutoUpdatingTextField(); // todo mal proque se borra siempre el de abajo, tengo que borrar el que correctponde
                Button(
                    onClick = { if (contador > 0) {contador--} }
                ) {
                    Text(text = "X")
                }
            }
        }

        FloatingActionButton(
            onClick = {if (contador < 2) { contador ++ }  },
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier.padding(top= 20.dp)
            // todo me gusatria poner un enable conatdor >=2 pero no existe
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar")
        }
    }
}


@Composable
fun AutoUpdatingTextField() {
    var texto by remember { mutableStateOf("") }

    TextField(
        label = { Text("Direccion") },
        value = texto,
        onValueChange = { texto = it },
        modifier = Modifier.padding(20.dp)
    )

}


