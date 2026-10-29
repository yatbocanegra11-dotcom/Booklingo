package com.example.iniciosesion.model

data class TraduccionResponse(
    val responseData: ResultadoTraduccion?,
    val responseStatus: Int?,
    val responseDetails: String?
)

data class ResultadoTraduccion(
    val translatedText: String?,
    val match: Double?
)