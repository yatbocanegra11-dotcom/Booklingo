package com.example.iniciosesion.network

import com.example.iniciosesion.model.TraduccionResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// Archivo que permite generar la peticion a la API de BookLingo
interface TraduccionApi {

    @GET("get")
    fun traducirTexto(
        @Query("q") texto: String,
        @Query("langpair") idiomas: String
    ): Call<TraduccionResponse>
}