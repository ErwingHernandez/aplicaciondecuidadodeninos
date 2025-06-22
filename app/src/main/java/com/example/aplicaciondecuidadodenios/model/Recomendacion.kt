package com.example.aplicaciondecuidadodenios.model

data class Recomendacion(
    val _id: String? = null,
    val edad_min_meses: Int,
    val edad_max_meses: Int,
    val recomendacion: String
)