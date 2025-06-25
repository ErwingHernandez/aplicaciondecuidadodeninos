package com.example.aplicaciondecuidadodenios.model

import com.google.gson.annotations.SerializedName

data class Nino(
    @SerializedName("_id") val _id: String? = null, // Asumimos que _id siempre viene y no es nulo
    @SerializedName("nombre") val nombre: String,
    @SerializedName("fecha_nacimiento") val fechaNacimiento: String?, // Cambiado a nullable String? por seguridad
    @SerializedName("sexo") val sexo: String,
    @SerializedName("identificacion") val identificacion: String?,
    @SerializedName("usuario_id") val usuarioId: String,
    @SerializedName("__v") val __v: Int? = null
)