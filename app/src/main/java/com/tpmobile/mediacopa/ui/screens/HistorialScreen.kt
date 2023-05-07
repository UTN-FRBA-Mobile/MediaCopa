package com.tpmobile.mediacopa.ui.screens

//import com.tpmobile.mediacopa.MainActivity.BottomMenu
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Composable
fun HistorialScreen(navController: NavController) {

    LazyColumn(
        modifier = Modifier
            .padding(16.dp) //Esto es para que la tarjetita tenga un margen a los costados
            .fillMaxWidth()
    ) {
        itemsIndexed(nombresGuardados) {index, nombres ->
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp) //ESPACIO entre tarjetitas
                    .fillMaxWidth(),
                backgroundColor = Color.LightGray //TODO MaterialTheme.colors.primary VER COMO FUNCIONA ESO PARA CAMBIARLO
            ) {
                Column(
                    modifier = Modifier.padding(16.dp) //ESPACIO ENTRE EL INICIO DEL TEXTO Y EL MARGEN IZQUIERDO DE LA TARJETA
                ) {
                    Text(
                        text = nombres.first,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = nombres.second,
                        fontSize = 16.sp
                    )
                    Text(
                        text = nombres.third,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }


}

val nombresGuardados = mutableStateListOf<Triple<String, String, String>>()

private const val NOMBRES_GUARDADOS_KEY = "nombresGuardados"

class MySharedPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getNombresGuardados(): List<Triple<String, String, String>> {
        val nombresGuardadosJson = sharedPreferences.getString(NOMBRES_GUARDADOS_KEY, null)
        return if (nombresGuardadosJson != null) {
            val type = object : TypeToken<List<Triple<String, String, String>>>() {}.type
            gson.fromJson(nombresGuardadosJson, type)
        } else {
            emptyList()
        }
    }

    fun guardarNombresGuardados(nombres: List<Triple<String, String, String>>) {
        val nombresGuardadosJson = gson.toJson(nombres)
        val editor = sharedPreferences.edit()
        editor.putString(NOMBRES_GUARDADOS_KEY, nombresGuardadosJson)
        editor.apply()
}}


@Composable
//Defino la funcion que debe ser llamada para agregar tarjetitas en el historial
fun agregarAHistorial(nombre1: String, nombre2: String, nombre3: String, sharedPreferences: MySharedPreferences) {

    nombresGuardados.clear()
    nombresGuardados.addAll(sharedPreferences.getNombresGuardados())
    nombresGuardados.add(Triple(nombre1, nombre2, nombre3))
    sharedPreferences.guardarNombresGuardados(nombresGuardados)


}