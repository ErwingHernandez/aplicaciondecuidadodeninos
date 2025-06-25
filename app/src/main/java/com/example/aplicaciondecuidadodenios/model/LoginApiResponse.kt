package com.example.aplicaciondecuidadodenios.model

import com.google.gson.annotations.SerializedName

data class LoginApiResponse(
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("usuario") val usuario: UsuarioResponse // <-- Aquí se anida tu objeto de usuario
)

// También necesitamos un modelo para el objeto 'usuario' que viene anidado,
// pero que NO tenga el campo 'contraseña' para la respuesta.
// Lo llamaremos UsuarioResponse para diferenciarlo del Usuario que usas para registrar.
data class UsuarioResponse(
    @SerializedName("_id") val _id: String, // Ahora debe ser String (no String?) porque tu API lo devuelve
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("rol") val rol: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("direccion") val direccion: String?,
    @SerializedName("__v") val __v: Int? = null // Opcional, si siempre viene, quita el '?' y el '=null'
)