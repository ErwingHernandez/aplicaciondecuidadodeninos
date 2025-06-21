package com.example.aplicaciondecuidadodenios.model

data class Recomendacion(
    val _id: String? = null,
    val edad_meses_min: Int,
    val edad_meses_max: Int,
    val contenido: String
)