package com.tpmobile.mediacopa.ui.screens

//import androidx.compose.foundation.layout.ColumnScopeInstance.align TODO lo comente porque no me corria el codigo y no se usaba
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.RestrictionsManager.RESULT_ERROR
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tpmobile.mediacopa.models.Address
import com.tpmobile.mediacopa.MapState
import com.tpmobile.mediacopa.MapViewModel
import com.tpmobile.mediacopa.model.AddressesItem
import com.tpmobile.mediacopa.model.Meeting
import com.tpmobile.mediacopa.model.RequestMeetings
import com.tpmobile.mediacopa.networking.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DireccionesViewModel(): ViewModel() {
    //@Preview(showBackground = true)
    @Composable
    fun DireccionesScreen(
        navController: NavController,
        lugar: String,
        viewModel: MapViewModel
    ) { // hay que comentar los parametros para poder usar el preview

        var addressEmpty = AddressesItem()

        var selectedPlaces by remember { mutableStateOf(mutableListOf<AddressesItem?>()) }

        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Agregar direcciones clickeando el boton",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(start = 20.dp, bottom = 20.dp, top = 20.dp),
            )

            var cantDirecciones by remember { mutableStateOf(2) }

            val addIcon by remember { mutableStateOf(mutableListOf<Boolean>()) }
            repeat(4) { addIcon.add(true) }

            var prueba by remember { mutableStateOf("") }

            Column {
                repeat(cantDirecciones) { index ->
                    Row(modifier = Modifier.padding(vertical = 10.dp)) {

                        if (index == 0) {
                            Button(
                                onClick = {
                                    var address = Address(
                                        streetAddress = "Mi ubicacion",
                                        latLong = LatLng(
                                            MapState.lastKnownLocation!!.latitude,
                                            MapState.lastKnownLocation!!.longitude
                                        ),
                                        type = Place.Type.STREET_ADDRESS,
                                        position = 0
                                    );

                                    if (selectedPlaces.any { x -> x?.position == 0 }) {
                                        selectedPlaces.removeAll { x -> x?.position == 0 }
                                    }

                                    selectedPlaces.add(address);
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.padding(5.dp),
                            ) {
                                Text(text = "Elegir mi ubicacion")
                            }
                        }

                        AutoUpdatingTextField(index, selectedPlaces)

                        // TODO: esto funciona pero solo cuando tocas un + o x de los botones
                        //  (o sea un click en la pantalla), sino no se actualiza

                        if (selectedPlaces.isNotEmpty()) {
                            var texto = buscarAdress(index, selectedPlaces);
                            Row(horizontalArrangement = Arrangement.SpaceAround) {
                                Text(texto)
                            }
                        }

                        if (index != 0) {
                            IconButton(
                                onClick = {
                                    if (addIcon[index]) {
                                        cantDirecciones++
                                    } else {
                                        selectedPlaces.removeAll { address -> address?.position == index }
                                        cantDirecciones--
                                    }
                                    addIcon[index] = !addIcon[index]
                                },
                            ) {
                                Icon(
                                    imageVector = if (addIcon[index] && cantDirecciones < 4) Icons.Default.Add else Icons.Default.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    agregarAHistorialYNavigateAMapa(
                        navController,
                        selectedPlaces,
                        lugar,
                        viewModel
                    )
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(5.dp),
            ) {
                Text(text = "Buscar punto medio")
            }
        }
    }

    var type: String = ""
    var lat: Double = 0.0;
    var lon: Double = 0.0;
    var streetAddress: String = ""
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
            }

            override fun onFailure(call: Call<Meeting>, t: Throwable) {
                Log.d("error", t.toString());
            }
        })
    }

    @Composable
    fun buscarAdress(index: Int, selectedPlaces: MutableList<Address?>): String {
        var texto =
            selectedPlaces.filter { addres -> addres?.position == index }.first()?.streetAddress;
        if (texto !== null) {
            return texto
        } else return "";
    }


    fun agregarAHistorialYNavigateAMapa(
        navController: NavController,
        selectedPlaces: MutableList<Address?>,
        lugar: String,
        viewModel: MapViewModel
    ) {
        val listOfAddresses = ArrayList<AddressesItem>()
        selectedPlaces.forEach {
            val newAddress =
                AddressesItem(lon = it?.latLong?.longitude, lat = it?.latLong?.latitude);
            listOfAddresses.add(newAddress)
        }
        val requestMiddlePointBody = RequestMeetings(
            type = lugar,
            addresses = listOfAddresses
        );
        type = "CAFE"
        lat = "37".toDouble()
        lon = "-122".toDouble()
        streetAddress = "Alamafuerte 1439"

//    getMiddlePoint(requestMiddlePointBody) //todo descomentar
        navController.navigate("Mapa/" + type + "/" + lat + "/" + lon + "/" + streetAddress) //todo funciona mal
    }

    @Composable
    fun AutoUpdatingTextField(index: Int, selectedPlaces: MutableList<Address?>) {
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
                        var address = Address(
                            streetAddress = placeResult?.value?.name,
                            latLong = placeResult.value?.latLng,
                            type = placeResult.value?.types?.get(0),
                            position = index
                        );

                        // si quiero cambiar la direccion de este boton, que la pise
                        if (selectedPlaces.any { x -> x?.position == index }) {
                            selectedPlaces.removeAll { x -> x?.position == index }
                        }

                        selectedPlaces.add(address);
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
            Button(
                onClick = launchMapInputOverlay,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(5.dp)
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Direcciones",
                    tint = Color.White
                )
            }
        }
    }

}