package com.example.iniciosesion.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Colocar la URL base de la API
    private const val BASE_URL = "https://api.mymemory.translated.net/"

    // Construir la conexion con la API
    val api: TraduccionApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TraduccionApi::class.java)
    }
}
