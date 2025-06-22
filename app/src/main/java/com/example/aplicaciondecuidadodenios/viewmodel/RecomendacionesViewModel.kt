package com.example.aplicaciondecuidadodenios.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicaciondecuidadodenios.model.Recomendacion
import com.example.aplicaciondecuidadodenios.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class RecomendacionesViewModel : ViewModel() {

    private val _recomendaciones = MutableStateFlow<List<Recomendacion>>(emptyList())
    val recomendaciones: StateFlow<List<Recomendacion>> = _recomendaciones

    init {
        Log.d("MiAppDebug", "RecomendacionesViewModel: Se ha inicializado el ViewModel.")
        cargarRecomendaciones()
    }

    private fun cargarRecomendaciones() {
        Log.d("MiAppDebug", "RecomendacionesViewModel: Iniciando carga de recomendaciones desde la API (usando Call).")

        ApiClient.apiService.obtenerRecomendaciones().enqueue(object :
            Callback<List<Recomendacion>> {
            override fun onResponse(call: Call<List<Recomendacion>>, response: Response<List<Recomendacion>>) {
                if (response.isSuccessful) {
                    val resultado = response.body()
                    if (resultado != null) {
                        _recomendaciones.value = resultado
                        Log.d("MiAppDebug", "RecomendacionesViewModel: Carga exitosa. Número de recomendaciones: ${resultado.size}")
                    } else {
                        Log.w("MiAppDebug", "RecomendacionesViewModel: Respuesta exitosa pero cuerpo nulo.")
                    }
                } else {
                    // La solicitud llegó al servidor, pero no fue 2xx
                    Log.e("MiAppDebug", "RecomendacionesViewModel: Error en la respuesta HTTP: Código ${response.code()}, Mensaje: ${response.message()}")
                    Log.e("MiAppDebug", "RecomendacionesViewModel: Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Recomendacion>>, t: Throwable) {
                // No se pudo conectar al servidor o hubo un error de red/IO
                Log.e("MiAppDebug", "RecomendacionesViewModel: Fallo de conexión o error: ${t.message}", t)
            }
        })
    }
}

