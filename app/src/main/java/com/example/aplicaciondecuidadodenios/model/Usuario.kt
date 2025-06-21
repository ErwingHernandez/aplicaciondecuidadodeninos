package com.example.aplicaciondecuidadodenios.model

data class Usuario(
    val _id: String? = null,
    val nombre: String,
    val correo: String,
    val contraseña: String,
    val rol: String,
    val telefono: String?,
    val direccion: String?
)