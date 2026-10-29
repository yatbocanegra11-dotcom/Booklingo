package com.example.iniciosesion.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {
   //colocarla base URl
    private const val BASE_URL = "https://api.mymemory.translated.net/"

    //construir la url completa
    val api : TraduccionApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .creat(TraduccionApi::class.java)
    }

}