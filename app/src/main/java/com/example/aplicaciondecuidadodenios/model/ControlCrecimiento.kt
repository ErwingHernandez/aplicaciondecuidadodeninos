package com.example.aplicaciondecuidadodenios.model

data class ControlCrecimiento(
    val _id: String? = null,
    val child_id: String,
    val fecha: String,
    val peso_kg: Double,
    val talla_cm: Double,
    val edad_meses: Int,
    val imc: Double?,
    val observaciones: String?
)