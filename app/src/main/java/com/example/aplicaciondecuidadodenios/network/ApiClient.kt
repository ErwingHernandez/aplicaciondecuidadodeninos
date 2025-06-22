package com.example.aplicaciondecuidadodenios.network

import okhttp3.OkHttpClient // Importar OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor // Importar el interceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit // Para configurar timeouts

object ApiClient {
    private const val BASE_URL = "https://api-de-cuidado.vercel.app/api/"

    val apiService: ApiService by lazy {
        // 1. Crear un interceptor de log para OkHttp
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            // Establece el nivel de log para ver los cuerpos de las peticiones/respuestas
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 2. Crear un cliente OkHttp y añadir el interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            // Opcional: Añadir timeouts si la red es lenta o inestable
            .connectTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para establecer la conexión
            .readTimeout(30, TimeUnit.SECONDS)    // Tiempo máximo para leer la respuesta
            .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo máximo para escribir la petición
            .build()

        // 3. Construir Retrofit usando el cliente OkHttp personalizado
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <-- ¡Importante! Usar nuestro cliente OkHttp
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}