// app/src/main/java/com/example/aplicaciondecuidadodenios/network/ApiService.kt
package com.example.aplicaciondecuidadodenios.network

import com.example.aplicaciondecuidadodenios.model.*
import retrofit2.Call
import retrofit2.http.*


interface ApiService{

    // Usuarios
    @POST("usuarios/create")
    fun registrarUsuario(@Body usuario: Usuario): Call<Usuario>

    @POST("usuarios/login") // Asumiendo que tu endpoint de login es algo como /usuarios/login
    fun loginUsuario(@Body loginRequest: LoginRequest): Call<Usuario> // O un objeto LoginResponse personalizado

    // Niños
    // ...

    //ControlCrecimiento
    @GET("/controles/{id}")
    fun obtenerControlesPorNino(@Path("id") idNino: String): Call<List<ControlCrecimiento>>

    // 🥗 Recomendaciones Nutricionales
    @GET("recomendaciones/")
    fun obtenerRecomendaciones(): Call<List<Recomendacion>>
}