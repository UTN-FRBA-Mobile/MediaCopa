package com.tpmobile.mediacopa.ui.screens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

//import com.tpmobile.mediacopa.MainActivity.BottomMenu
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import com.tpmobile.mediacopa.MapState
import com.tpmobile.mediacopa.R
import com.tpmobile.mediacopa.model.AddressesItem
import com.tpmobile.mediacopa.model.Historial
import com.tpmobile.mediacopa.model.Meeting
import com.tpmobile.mediacopa.networking.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    //  AppContext.sharedPreferences.borrarNombresGuardados()
    botonApretado.value=false

}

@Composable
fun Intermediario(){
    // agregarAHistorial("b","a","a","2" )
    botonApretado2.value=false

}

var botonApretado = mutableStateOf(false)
var botonApretado2 = mutableStateOf(false)
val selectedList =  mutableStateListOf<Boolean>()
val primeraVuelta = mutableStateOf(false)
val primeraVueltaAlfa = mutableStateOf(false)
val segundoClick = mutableStateOf(false)
var tarjetaApretada = mutableStateOf(false)
var historial  = mutableStateListOf<Historial>()

@Composable
fun Tacho(count:Int){
    Box(Modifier.fillMaxSize( )) {
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
                        botonApretado.value = true
                        primeraVuelta.value = true


                        if (primeraVueltaAlfa.value) {
                            segundoClick.value = true
                            primeraVuelta.value = false
                        }
                        primeraVueltaAlfa.value = true

                    }
                }

        )
    }
}

@Composable
fun CrearListaSeleccion(count: Int){
    if(botonApretado.value and primeraVuelta.value){

        for (i in 0 until count) {
            selectedList.add(false)
        }
        primeraVuelta.value=false
    }

}

@Composable
fun EliminarSeleccionados( count: Int){
    var countAux= count
    if(segundoClick.value){
        var i=0
        while(i < countAux){
            if(selectedList[i]){
                borrarSeleccionado(i)
                countAux--
                selectedList.removeAt(i)
                i--
            }
            i++
        }
        primeraVuelta.value=false
        botonApretado.value = false
        segundoClick.value=false
        primeraVueltaAlfa.value=false
        selectedList.clear()
    }
}

@Composable
fun BotonPruebas(){

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

}

//TODO()
@Composable
fun RepetirBusqueda(index: Int, navController: NavController) {
    // ADD PUNTO MEDIO TO MapState
    val historialItem = historial[index].meetingAddress
    val newMeeting = Meeting(type = historialItem?.type, lat = historialItem?.lat, lon = historialItem?.lon)
    MapState.midpointAddress = newMeeting
    // ADD ADDRESSES TO MapState
    MapState.otherAddresses = historial[index].addresses as List<AddressesItem>?
    navController.navigate("Mapa")
}

@Composable
fun Tarjetas(navController: NavController){
    var indice=0
    Box(modifier = Modifier
        .fillMaxSize()
        .offset { IntOffset(0, 200) }) { // ESTO HACE QUE EL INICIO DEL BOX SEA 200 PIXELES MAS ABAJO
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 15.dp)
                .padding(bottom = 100.dp)
                .fillMaxWidth()
        ) {

            itemsIndexed(historial) { index, instanciaHistorial ->

                Card(
                    modifier = Modifier
                        .padding(vertical = 8.dp) //ESPACIO entre tarjetitas
                        .fillMaxWidth()
                        .let {
                            if (!botonApretado.value) {
                                it.clickable { tarjetaApretada.value = true
                                    indice=index}
                            } else {
                                it
                            }
                        },
                    backgroundColor = Color.LightGray //TODO MaterialTheme.colors.primary VER COMO FUNCIONA ESO PARA CAMBIARLO
                )
                {
                    if (tarjetaApretada.value) {
                        RepetirBusqueda(indice, navController)
                        tarjetaApretada.value = false
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

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
                            var imagen = R.drawable.history
                            var descripcion = "hola"
                            if (instanciaHistorial.meetingAddress?.type == "CAFE") {
                                imagen = R.drawable.local_cafe
                                var descripcion = "Cafe"
                            } else if (instanciaHistorial.meetingAddress?.type == "RESTAURANT") {
                                imagen = R.drawable.restaurant_menu
                                var descripcion = "Restaurante"
                            } else if (instanciaHistorial.meetingAddress?.type == "SHOPPING_MALL") {
                                imagen = R.drawable.storefront
                                var descripcion = "Tienda"
                            } else {
                                imagen = R.drawable.location
                                var descripcion = "Punto medio"
                            }
                            Image(
                                painter =
                                painterResource(imagen),
                                contentDescription = descripcion,
                                modifier = Modifier.fillMaxSize(),

                                )
                        }
                        val newTitle = if(instanciaHistorial.meetingAddress?.type == "ADDRESS"){
                            "Lat: " + instanciaHistorial.meetingAddress?.lat + ", Lon: " + instanciaHistorial.meetingAddress?.lon
                        } else {
                            instanciaHistorial.meetingAddress?.name.toString()
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
                        ) {
                            Text(

                                text = newTitle,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = instanciaHistorial.addresses?.get(0)?.streetAddress.toString(),
                                fontSize = 20.sp
                            )
                            Text(
                                text = instanciaHistorial.addresses?.get(1)?.streetAddress.toString(),
                                fontSize = 20.sp
                            )
                            if (instanciaHistorial.addresses?.size!! > 2) {
                                Text(
                                    text = "...",
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Image(
                            painter = painterResource(R.drawable.history),
                            contentDescription = "Historial",
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterVertically)
                                .padding(end = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


//TODO HACER ESTA FUNCION MAS LEGIBLE
@Composable
//todoo lo de abajo hace que se muestren por pantalla las tarjetitas
fun HistorialScreen(navController: NavController) {
    var count = historial.size
    Tacho(count)
    CrearListaSeleccion(count)
    EliminarSeleccionados(count)
    Tarjetas(navController)
}

fun getHistorial() {
    val retrofitBuilder =
        Retrofit.Builder()
            .baseUrl("http://192.168.0.110:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    val retrofitData = retrofitBuilder.getHistorial();
    retrofitData.enqueue(object: Callback<List<Historial>>{
        override fun onResponse(call: Call<List<Historial>>, response: Response<List<Historial>>) {
            historial.clear()
            historial.addAll(response.body()!!)
        }

        override fun onFailure(call: Call<List<Historial>>, t: Throwable) {
            Log.d("TAG", t.toString());
        }
    })
}

fun deleteFromHistorial(id: String) {
    val retrofitBuilder =
        Retrofit.Builder()
            .baseUrl("http://192.168.0.110:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    val retrofitData = retrofitBuilder.deleteFromHistorial(id);
    retrofitData.enqueue(object: Callback<String>{
        override fun onResponse(call: Call<String>, response: Response<String>) {

        }

        override fun onFailure(call: Call<String>, t: Throwable) {
            Log.d("TAG", t.toString());
        }
    })
}

fun borrarSeleccionado(i: Int) {
    deleteFromHistorial(historial[i].id.toString())
    historial.removeAt(i)
}
