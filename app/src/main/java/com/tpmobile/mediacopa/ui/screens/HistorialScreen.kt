package com.tpmobile.mediacopa.ui.screens

//import com.tpmobile.mediacopa.MainActivity.BottomMenu
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import com.tpmobile.mediacopa.R

/*
CADA VEZ QUE SE QUIERA AGREGAR ALGO AL HISTORIAL SE DEBE HACER LO SIGUIENTE
val context = LocalContext.current
val sharedPreferences = MySharedPreferences(context)
agregarAHistorial("b","a","a",sharedPreferences)

 */

/*
SI SE QUIERE BORRAR TODOO EL HISTORIAL
val context = LocalContext.current
val sharedPreferences = MySharedPreferences(context)
                    sharedPreferences.borrarNombresGuardados()*/

@Composable
fun BotonDelete(){
    AppContext.sharedPreferences.borrarNombresGuardados()
    botonApretado.value=false

}

@Composable
fun Intermediario(){
    agregarAHistorial("b","a","a","2" )
    botonApretado2.value=false

}

var botonApretado = mutableStateOf(false)
var botonApretado2 = mutableStateOf(false)
var selectedCards = mutableStateOf(emptyList<Int>())
var isChecked= mutableStateOf(false)
val selectedList =  mutableStateListOf<Boolean>()
val primeraVuelta = mutableStateOf(false)
val primeraVueltaAlfa = mutableStateOf(false)
val segundoClick = mutableStateOf(false)

//TODO HACER ESTA FUNCION MAS LEGIBLE
@Composable
//todoo lo de abajo hace que se muestren por pantalla las tarjetitas
fun HistorialScreen(navController: NavController) {
    val count = AppContext.nombresGuardados.size

    //BotonDelete()
    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.delete),
            contentDescription = "Delete",
            modifier = Modifier
                .size(65.dp)
                .padding(end = 16.dp, top = 16.dp)
                .background(Color.Transparent)
                .align(Alignment.TopEnd)
                .clickable {
                    if (count != 0) {
                        if (primeraVueltaAlfa.value) {
                            segundoClick.value = true
                        }
                        botonApretado.value = true
                        primeraVuelta.value = true
                        primeraVueltaAlfa.value = true
                    }
                }

        )
    }
   if(botonApretado.value and primeraVuelta.value){

       for (i in 0 until count) {
           selectedList.add(false)
       }
    primeraVuelta.value=false
   }
    if(segundoClick.value){
        for (i in 0 until count) {
            if(selectedList[i]){
                AppContext.sharedPreferences.borrarSeleccionado(i)
                AppContext.sharedPreferences.borrarSeleccionadosSesiones()

                }
        }
        primeraVuelta.value=false
        botonApretado.value = false
        segundoClick.value=false
        primeraVueltaAlfa.value=false
    }

    Box(Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.delete),
            contentDescription = "Delete",
            modifier = Modifier
                .size(65.dp)
                .padding(end = 16.dp, top = 16.dp)
                .background(Color.Transparent)
                .align(Alignment.TopStart)
                .clickable { botonApretado2.value = true }

        )
    }
    if(botonApretado2.value){
        Intermediario()} //TODO DEJO ESTO PARA USAR PARA TESTEAR A FUTURO

    Box(modifier = Modifier.fillMaxSize()
        .offset { IntOffset(0, 200) }) { // ESTO HACE QUE EL INICIO DEL BOX SEA 200 PIXELES MAS ABAJO
    LazyColumn(
        modifier = Modifier
            .padding(16.dp) //Esto es para que la tarjetita tenga un margen a los costados
            .fillMaxWidth()
    ) {

        itemsIndexed(AppContext.nombresGuardados) {index, nombres ->
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

                    if (botonApretado.value) {
                        Checkbox(
                            checked = selectedList[index],
                            onCheckedChange = { isChecked ->
                                selectedList[index] = isChecked
                            })

                    }

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
                        modifier = Modifier.weight(1f)
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

    }
}

data class Cuadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)



private const val NOMBRES_GUARDADOS_KEY = "nombresGuardadoss"

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

    fun guardarNombresGuardados() {
        val nombresGuardadosJson = gson.toJson(AppContext.nombresGuardados)
        val editor = sharedPreferences.edit()
        editor.putString(NOMBRES_GUARDADOS_KEY, nombresGuardadosJson)
        editor.apply()

    }
    fun borrarNombresGuardados() {
        val editor = sharedPreferences.edit()
        editor.remove(NOMBRES_GUARDADOS_KEY)
        editor.apply()
       listaVacia()
    }

    fun borrarSeleccionadosSesiones() {
        val editor = sharedPreferences.edit()
        editor.remove(NOMBRES_GUARDADOS_KEY)
        editor.apply()
        guardarNombresGuardados()
    }

    fun listaVacia() {
        AppContext.nombresGuardados.clear()
        AppContext.nombresGuardados.addAll(getNombresGuardados())
    }

    fun borrarSeleccionado(i: Int) {
        AppContext.nombresGuardados.removeAt(i)
    }

}

//ACA SE AGREGA ALGO NUEVO AL HISTORIAL
@Composable
//Defino la funcion que debe ser llamada para agregar tarjetitas en el historial
fun agregarAHistorial(nombre1: String, nombre2: String, nombre3: String,nombre4: String) {

    AppContext.nombresGuardados.clear()
    AppContext.nombresGuardados.addAll(AppContext.sharedPreferences.getNombresGuardados())
    AppContext.nombresGuardados.add(Cuadruple(nombre1, nombre2, nombre3,nombre4))
    AppContext.sharedPreferences.guardarNombresGuardados()


}

object AppContext {
    lateinit var context: Context
    lateinit var sharedPreferences: MySharedPreferences
    var nombresGuardados = mutableStateListOf<Cuadruple<String, String, String, String>>()

    fun init(context: Context) {
        this.context = context.applicationContext
        sharedPreferences = MySharedPreferences(context.applicationContext)
        nombresGuardados.clear()
        nombresGuardados.addAll(sharedPreferences.getNombresGuardados())
    }

}