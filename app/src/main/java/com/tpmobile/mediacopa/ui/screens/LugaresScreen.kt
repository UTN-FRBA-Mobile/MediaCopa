package com.tpmobile.mediacopa.ui.screens

import android.graphics.Color
import android.icu.text.CaseMap.Title
import android.text.Layout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier


//@Preview(showBackground = true)
@Composable
fun LugaresScreen(navController: NavController) { // hay que comentar los parametros para poder usar el preview

    val options = listOf("Cafes", "Restaurantes", "Tiendas", "Punto medio")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(options[0]) }

    Column (
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.padding(20.dp),
       ){
        Text(
            text = "Donde te gustaria encontrarte?",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 40.dp),

            )
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = selectedOption == option,
                    onClick = { onOptionSelected(option) }
                )
                Text(
                    text = option,
                )
            }
        }

        Button(
            onClick = { /* todo hacer que valla a direcciones */ },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(5.dp)
        ) {
            Text(text = "Continuar" )
        }
    }
}


