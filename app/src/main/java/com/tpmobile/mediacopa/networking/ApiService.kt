package com.tpmobile.mediacopa.networking

import com.google.gson.JsonObject
import com.tpmobile.mediacopa.model.Historial
import com.tpmobile.mediacopa.model.Meeting
import com.tpmobile.mediacopa.model.RequestMeetings
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("punto_medio")
    fun getMiddlePoint(
        @Body arrayOfAddresses: RequestMeetings
    ): Call<Meeting>

    @GET("historial")
    fun getHistorial(): Call<List<Historial>>
    @DELETE("historial/{id}")
    fun deleteFromHistorial(
        @Path("id") id: String
    ): Call<String>
}