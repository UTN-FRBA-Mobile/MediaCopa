package com.tpmobile.mediacopa.ui.screens

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.RestrictionsManager.RESULT_ERROR
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tpmobile.mediacopa.MapState
import com.tpmobile.mediacopa.model.AddressesItem
import com.tpmobile.mediacopa.model.Meeting
import com.tpmobile.mediacopa.model.RequestMeetings
import com.tpmobile.mediacopa.networking.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


var selectedPlaces =  mutableStateListOf<AddressesItem>()
var type: String = ""
var lat: Double = 0.0;
var lon: Double = 0.0;
var streetAddress: String = ""
var miUbi : Boolean = false;


class DireccionesViewModel(): ViewModel() {
    //@Preview(showBackground = true)
    @Composable
    fun DireccionesScreen(navController: NavController, lugar: String, viewModel: MapViewModel) {

        if(selectedPlaces.size >= 2) {
            Box(contentAlignment = Alignment.TopEnd) {
                Button(
                    onClick = {
                        selectedPlaces.clear()
                        miUbi = false
                        MapState.midpointAddress = null
                        MapState.otherAddresses = null
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(text = "Limpiar")
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Agregar direcciones clickeando el boton",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(start = 30.dp, end = 8.dp, bottom = 30.dp, top = 200.dp),
            )

            Row() {
                AutoUpdatingTextField()

                // Deshabilitado si ya se clickeo, asi no se esta borrando y agregando a la pag
                Button(
                    onClick = {
                        var address = AddressesItem(
                            streetAddress = "Mi ubicacion", //por favor no cambiar porque hay codigo que depende de este nombre
                            lat = MapState.lastKnownLocation!!.latitude,
                            lon = MapState.lastKnownLocation!!.longitude
                        );
                        selectedPlaces.add(address)
                        miUbi = true;
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(start=5.dp),
                    enabled = !miUbi
                ) {
                    Text(text = "Mi ubicacion")
                }
            }

            mostrarAddress()

            if(selectedPlaces.size >= 2){
                Box(
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        onClick = {
                            agregarAHistorialYNavigateAMapa(
                                navController,
                                lugar,
                                viewModel
                            )
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(text = "Buscar punto medio")
                    }
                }
            }
        }
    }
}



    @Composable
    fun mostrarAddress(){
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 30.dp, vertical = 15.dp)
                .fillMaxWidth()
        ) {

            itemsIndexed(selectedPlaces) { index, place ->
                Row() {
                    Text(text = place?.streetAddress.toString(),
                        modifier = Modifier.padding(vertical = 15.dp)
                            )
                    IconButton(
                        onClick = {
                            if(selectedPlaces[index].streetAddress.toString() == "Mi ubicacion"){
                                miUbi= false;
                            }
                            selectedPlaces.removeAt(index)
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Eliminar"
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun AutoUpdatingTextField() {
        val context = LocalContext.current;
        var placeResult = remember { mutableStateOf<Place?>(null) }

        val intentLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { it ->
            when (it.resultCode) {
                RESULT_OK -> {
                    it.data?.let {
                        var place = Autocomplete.getPlaceFromIntent(it);
                        placeResult.value = place;
                        var address = AddressesItem(
                            streetAddress = placeResult?.value?.name,
                            lat = placeResult.value?.latLng?.latitude,
                            lon= placeResult.value?.latLng?.longitude,

                            );
                        selectedPlaces.add(address)
                    }
                }
                RESULT_ERROR -> {
                    it.data?.let {
                        val status = Autocomplete.getStatusFromIntent(it)
                        Log.i("MAP_ACTIVITY", "Place: FAIL")
                    }
                }
                RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }

        val launchMapInputOverlay = {
//        Places.initialize(context, BuildConfig.MAPS_API_KEY)
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.TYPES
            )
            val intent = Autocomplete
                .IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(context)
            intentLauncher.launch(intent)
        }

        Column {
            // Deshabilitado si ya se clickeo, asi no se esta borrando y agregando a la pag
            Button(
                onClick = launchMapInputOverlay,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(start = 30.dp, end=5.dp),
                enabled = selectedPlaces.size < 4
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Direcciones",
                    tint = Color.White
                )
            }
        }
    }

    fun agregarAHistorialYNavigateAMapa(navController: NavController,lugar: String,viewModel: MapViewModel) {
        val listOfAddresses = ArrayList<AddressesItem>()
        selectedPlaces.forEach {
            val newAddress =
                AddressesItem(lon = it?.lon, lat = it?.lat, streetAddress = it?.streetAddress);
            listOfAddresses.add(newAddress)
        }
        val requestMiddlePointBody = RequestMeetings(
            type = lugar,
            addresses = listOfAddresses
        );

        //todo borrar eso harcode y usar la funcion de abajo
        type = "CAFE"
        lat = "-34.741876".toDouble()
        lon = "-58.409036".toDouble()
        streetAddress = "Alamafuerte 1439"

        // Seteo todas las direcciones en el estado para ser mostradas en el mapa
        MapState.otherAddresses = selectedPlaces.toList()

        //getMiddlePoint(requestMiddlePointBody) //todo descomentar

        navController.navigate("Mapa")
    }


    fun getMiddlePoint(requestMiddlePointBody: RequestMeetings) {
        val retrofitBuilder =
            Retrofit.Builder()
                .baseUrl("http://192.168.1.44:8081/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        val retrofitData = retrofitBuilder.getMiddlePoint(requestMiddlePointBody);
        retrofitData.enqueue(object : Callback<Meeting> {
            override fun onResponse(call: Call<Meeting>, response: Response<Meeting>) {
                val responseBody = response.body()!!
                type = responseBody.type.toString()
                lat = responseBody.lat.toString().toDouble()
                lon = responseBody.lon.toString().toDouble()
                streetAddress = responseBody.streetAddress.toString()

                MapState.midpointAddress = AddressesItem(streetAddress, lon, lat)
            }

            override fun onFailure(call: Call<Meeting>, t: Throwable) {
                Log.d("error", t.toString());
            }
        })
    }