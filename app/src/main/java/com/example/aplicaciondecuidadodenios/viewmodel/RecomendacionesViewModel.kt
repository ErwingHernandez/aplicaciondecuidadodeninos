package com.example.aplicaciondecuidadodenios.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.aplicaciondecuidadodenios.model.Recomendacion
import com.example.aplicaciondecuidadodenios.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecomendacionesViewModel: ViewModel() {
    private val _recomendaciones = MutableStateFlow<List<Recomendacion>>(emptyList())
    val recomendaciones: StateFlow<List<Recomendacion>> = _recomendaciones

    init {
        cargarRecomendaciones()
    }

    private fun cargarRecomendaciones() {
        ApiClient.apiService.obtenerRecomendaciones().enqueue(object :
            Callback<List<Recomendacion>> {
            override fun onResponse(
                call: Call<List<Recomendacion>>,
                response: Response<List<Recomendacion>>
            ) {
                if (response.isSuccessful) {
                    _recomendaciones.value = response.body() ?: emptyList()
                } else {
                    Log.e("RecomendacionesViewModel", "Respuesta no exitosa: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Recomendacion>>, t: Throwable) {
                Log.e("RecomendacionesViewModel", "Error al obtener recomendaciones", t)
            }
        })
    }
}

