package com.example.aplicaciondecuidadodenios.model

data class Nino(
    val _id: String? = null,
    val nombre: String,
    val fecha_nacimiento: String,
    val sexo: String,
    val identificacion: String?,
    val usuario_id: String
)