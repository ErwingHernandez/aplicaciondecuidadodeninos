package com.example.aplicaciondecuidadodenios.network

import com.example.aplicaciondecuidadodenios.model.*
import retrofit2.Call
import retrofit2.http.*



interface ApiService{

    // Usuarios
    @POST("usuarios/create/")
    fun registrarUsuario(@Body usuario: Usuario): Call<Usuario>

    @POST("usuarios/login/")
    fun loginUsuario(@Body loginRequest: LoginRequest): Call<Usuario>

    // Niños

    @GET("ninos/porusuario/{id}/")
    fun obtenerNinosPorUsuario(@Path("id") usuario_id: String): Call<List<Nino>>

    //ControlCrecimiento

    @GET("controlcrecimiento/pornino/{id}")
    fun obtenerControlesPorNino(@Path("id") child_id: String): Call<List<ControlCrecimiento>>

    // 🥗 Recomendaciones Nutricionales

    @GET("recomendaciones/")
    fun obtenerRecomendaciones(): Call<List<Recomendacion>>

    @POST("ninos/")
    fun registrarNino(@Body nuevoNino: Nino): Call<Nino>

    @POST("controlcrecimiento/create/")
    fun registrarControl(@Body control: ControlCrecimiento): Call<ControlCrecimiento>

}

