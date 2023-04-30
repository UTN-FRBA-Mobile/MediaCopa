package com.tpmobile.mediacopa.ui.screens

import android.icu.text.CaseMap.Title
import android.text.Layout
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.material.MaterialTheme


@Preview(showBackground = true)
@Composable
fun DireccionesScreen(/*navController: NavController*/) { // hay que comentar los parametros para poder usar el preview

    val options = listOf("Cafes", "Restaurantes", "Tiendas", "Punto medio")
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(options[0]) }

    Column {
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
    }
}


