package com.example.aplicaciondecuidadodenios.network

import com.example.aplicaciondecuidadodenios.model.*
import retrofit2.Call
import retrofit2.http.*



interface ApiService{

    // Usuarios
    @POST("usuarios")
    fun registrarUsuario(@Body usuario: Usuario): Call<Usuario>

    // Niños


    //ControlCrecimiento

    @GET("controles/{id}")
    fun obtenerControlesPorNino(@Path("id") idNino: String): Call<List<ControlCrecimiento>>

    // 🥗 Recomendaciones Nutricionales

    @GET("recomendaciones")
    fun obtenerRecomendaciones(): Call<List<Recomendacion>>

}

