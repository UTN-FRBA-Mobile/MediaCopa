package com.tpmobile.mediacopa.ui.screens

//import com.tpmobile.mediacopa.MainActivity.BottomMenu
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.res.painterResource
import com.tpmobile.mediacopa.R


/*
CADA VEZ QUE SE QUIERA AGREGAR ALGO AL HISTORIAL SE DEBE HACER LO SIGUIENTE
val context = LocalContext.current
val sharedPreferences = MySharedPreferences(context)
agregarAHistorial("b","a","a",sharedPreferences)

 */

/*
SI SE QUIERE BORRAR TODOO EL HISTORIAL
Val context = LocalContext.current
val sharedPreferences = MySharedPreferences(context)
                    sharedPreferences.borrarNombresGuardados()*/



//TODO HACER ESTA FUNCION MAS LEGIBLE
@Composable
//todoo lo de abajo hace que se muestren por pantalla las tarjetitas
fun HistorialScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
    LazyColumn(
        modifier = Modifier
            .padding(16.dp) //Esto es para que la tarjetita tenga un margen a los costados
            .fillMaxWidth()
    ) {
        item { Spacer(modifier = Modifier.height(65.dp)) }
        itemsIndexed(nombresGuardados) {index, nombres ->
            Card(
                modifier = Modifier
                    .padding(vertical = 8.dp) //ESPACIO entre tarjetitas
                    .fillMaxWidth(),
                backgroundColor = Color.LightGray //TODO MaterialTheme.colors.primary VER COMO FUNCIONA ESO PARA CAMBIARLO
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){

                    Box(
                        modifier = Modifier
                            .size(65.dp)
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically)
                            .padding(start = 16.dp)
                    ) {
                        var imagen =R.drawable.history
                        var descripcion="hola"
                        if(nombres.fourth=="1"){
                            imagen= R.drawable.local_cafe
                            var descripcion="Cafe"
                        }else if(nombres.fourth=="2"){
                            imagen= R.drawable.restaurant_menu
                            var descripcion="Restaurante"
                        }else if(nombres.fourth=="2"){
                            imagen= R.drawable.storefront
                            var descripcion="Tienda"
                        }else{
                            imagen= R.drawable.location
                            var descripcion="Punto medio"
                        }
                        Image(
                            painter =
                             painterResource(imagen),
                            contentDescription = descripcion,
                            modifier = Modifier.fillMaxSize(),

                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f) // to take up all available horizontal space
                            .padding(start = 8.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                    )  {
                    Text(
                        text = nombres.first,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = nombres.second,
                        fontSize = 20.sp
                    )
                    Text(
                        text = nombres.third,
                        fontSize = 20.sp
                    )
                }

                    //TODO EN CASO QUE NO ANDE/QUIERAN CAMBIAR LA IMAGEN, ACA ESTA LA SEGUNDA OPCION
                    /*Icon(
                    Icons.Filled.Refresh,
                        contentDescription = "Historial",
                        modifier = Modifier.size(48.dp)
                            .align(Alignment.CenterVertically)
                            .padding(end = 8.dp)
                    )*/
                    Image(
                        painter = painterResource(R.drawable.history),
                        contentDescription = "Historial",
                        modifier = Modifier.size(48.dp)
                            .align(Alignment.CenterVertically)
                            .padding(end = 8.dp)
                    )


                }
            }
        }
    }

        Image(
            painter = painterResource(R.drawable.delete),
            contentDescription = "Delete",
            modifier = Modifier
                .size(65.dp)
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 16.dp)

                )

    }
}

data class Cuadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)


//LISTA PARA LAS TARJETITAS
val nombresGuardados = mutableStateListOf<Cuadruple<String, String, String,String>>()

private const val NOMBRES_GUARDADOS_KEY = "nombresGuardados"

//ESTA CLASE PERMITE QUE SE GUARDE ENTRE SESIONES EL HISTORIAL (TIENE UNA FUNCION PARA BORRARLO ENTERO, PARA LAS PRUEBAS)
//TODO AGREGAR FUNCION QUE PERMITA BORRAR DE A UNO
class MySharedPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getNombresGuardados(): List<Cuadruple<String, String, String,String>> {
        val nombresGuardadosJson = sharedPreferences.getString(NOMBRES_GUARDADOS_KEY, null)
        return if (nombresGuardadosJson != null) {
            val type = object : TypeToken<List<Cuadruple<String, String, String,String>>>() {}.type
            gson.fromJson(nombresGuardadosJson, type)
        } else {
            emptyList()
        }
    }

    fun guardarNombresGuardados(nombres: List<Cuadruple<String, String, String,String>>) {
        val nombresGuardadosJson = gson.toJson(nombres)
        val editor = sharedPreferences.edit()
        editor.putString(NOMBRES_GUARDADOS_KEY, nombresGuardadosJson)
        editor.apply()

    }
    fun borrarNombresGuardados() {
        val editor = sharedPreferences.edit()
        editor.remove(NOMBRES_GUARDADOS_KEY)
        editor.apply()
    }

}

//ACA SE AGREGA ALGO NUEVO AL HISTORIAL
@Composable
//Defino la funcion que debe ser llamada para agregar tarjetitas en el historial
fun agregarAHistorial(nombre1: String, nombre2: String, nombre3: String,nombre4: String, sharedPreferences: MySharedPreferences) {

    nombresGuardados.clear()
    nombresGuardados.addAll(sharedPreferences.getNombresGuardados())
    nombresGuardados.add(Cuadruple(nombre1, nombre2, nombre3,nombre4))
    sharedPreferences.guardarNombresGuardados(nombresGuardados)


}