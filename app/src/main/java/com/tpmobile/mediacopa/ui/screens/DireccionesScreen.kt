package com.tpmobile.mediacopa.ui.screens

//import androidx.compose.foundation.layout.ColumnScopeInstance.align TODO lo comente porque no me corria el codigo y no se usaba
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.RestrictionsManager.RESULT_ERROR
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.tpmobile.mediacopa.BuildConfig
import com.tpmobile.mediacopa.models.Address
import com.tpmobile.mediacopa.models.AgregarAHistorialInputModel
import com.tpmobile.mediacopa.MainActivity
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


//@Preview(showBackground = true)
@Composable

fun DireccionesScreen(navController: NavController, lugar: String, placesClient: PlacesClient?,
                      viewModel : MapViewModel, fusedLocationProviderClient: FusedLocationProviderClient) { // hay que comentar los parametros para poder usar el preview

    val context = LocalContext.current
    val selectedPlaces by remember { mutableStateOf(mutableListOf<Address?>()) }
    val suggestionsList by remember { mutableStateOf(MutableList(4) { emptyList<AutocompletePrediction>() }) } // cada direccion tiene su lista de sugerencias
    var textFieldValues by remember { mutableStateOf( mutableListOf<TextFieldValue>() ) }

    //test con 1 solo
    var suggestions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    var textFieldValue by remember { mutableStateOf( TextFieldValue() ) }


    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "Agregar direcciones clickeando el boton",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 20.dp , top= 20.dp),
            )

        var cantDirecciones by remember { mutableStateOf(2) }
        var geo by remember { mutableStateOf(false) }

        Column {
            repeat(cantDirecciones) { index ->
                Row(modifier = Modifier.padding(vertical=10.dp)) {

                    if(index == 0){
                        Button(onClick = {geo = true } ,
                                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.padding(5.dp),
                        ) {
                            Text(text = "Elegir mi ubicacion")
                        }
//                        Log.e("lastKnownLocation: ", (MapState.lastKnownLocation!!.latitude).toString()) //borar todo
                    }

                    var place = AutoUpdatingTextField();


                    Log.i("LUGAR SELECCIONADO", "Place: ${place.value?.address}, ${place.value?.name} - LatLong ${place.value?.latLng} - Tipo ${place.value?.types}");


                    Row() {
                        if (geo && index == 0) {
                            Text("Mi ubicacion")
                        } else if (place?.value != null) {
                            Text("Dirección ${index + 1}: ${place.value?.name}")
                        }
                    }

                    var address : Address

                    if(geo && index == 0){
                        address = Address(
                            streetAddress = "Mi ubicacion",
                            latLong =  LatLng(MapState.lastKnownLocation!!.latitude, MapState.lastKnownLocation!!.longitude),
                            type = Place.Type.STREET_ADDRESS
                        );

                    }else{

                        address = Address(
                            streetAddress= place?.value?.address,
                            latLong= place.value?.latLng,
                            type = place.value?.types?.get(0)
                        );
                    }

                    selectedPlaces.add(index, address);


                    if (cantDirecciones > 2) { // a partir de 3 direcc, aparece un -
                        IconButton(
                            onClick = { cantDirecciones -- },
//                            modifier = Modifier.padding(top= 20.dp),
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = null)
                        }
                    }

                }

            }
            if (cantDirecciones < 4) { // hasta 3 direcc, aparece un +
                FloatingActionButton(
                    onClick = { cantDirecciones ++ },
                    backgroundColor = MaterialTheme.colors.primary,
                    modifier = Modifier.padding(top= 20.dp),
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            }

        }
        Button(
            onClick =  { agregarAHistorialYNavigateAMapa(navController, selectedPlaces, lugar, viewModel)  },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(5.dp),
        ) {
            Text(text = "Buscar punto medio" )
        }
    }
}

var type = String()
var lat = Float;
var lon = Float;
fun getMiddlePoint(requestMiddlePointBody: RequestMeetings) {
    val retrofitBuilder =
        Retrofit.Builder()
            .baseUrl("http://192.168.0.110:8081/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    val retrofitData = retrofitBuilder.getMiddlePoint(requestMiddlePointBody);
    retrofitData.enqueue(object: Callback<Meeting> {
        override fun onResponse(call: Call<Meeting>, response: Response<Meeting>) {
            val responseBody = response.body()!!
            type = responseBody.type.toString()
            lat = responseBody.lat as Float.Companion
            lon = responseBody.lon as Float.Companion
        }

        override fun onFailure(call: Call<Meeting>, t: Throwable) {
Log.d("error", t.toString());
        }
    })
}


fun agregarAHistorialYNavigateAMapa(navController : NavController, selectedPlaces: MutableList<Address?> , lugar : String, viewModel: MapViewModel) {
    val listOfAddresses = ArrayList<AddressesItem>()
    selectedPlaces.forEach {
        val newAddress = AddressesItem(lon = it?.latLong?.longitude, lat = it?.latLong?.latitude);
        listOfAddresses.add(newAddress)
    }
    val requestMiddlePointBody = RequestMeetings(
        type= lugar,
        addresses = listOfAddresses
    );
    getMiddlePoint(requestMiddlePointBody)
    navController.navigate("Mapa/"+type+"/"+ lat.toString()+"/"+lon.toString())
}

@Composable
fun AutoUpdatingTextField(): MutableState<Place?> {
    val context = LocalContext.current;
    var placeResult = remember { mutableStateOf<Place?>(null) }

    val intentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ){ it ->
        when (it.resultCode) {
            RESULT_OK -> {
                it.data?.let {
                    var place = Autocomplete.getPlaceFromIntent(it);
                    placeResult.value = place;
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
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.TYPES)
        val intent = Autocomplete
            .IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .build(context)
        intentLauncher.launch(intent)
    }

    Column {

        Button(onClick = launchMapInputOverlay) {
//            Text("Ingresar dirección")
            Icon(Icons.Filled.LocationOn,
                contentDescription = "Direcciones",
                tint = Color.White)
        }

    }

    return placeResult;
}

