package com.example.aplicaciondecuidadodenios.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aplicaciondecuidadodenios.model.Nino
import com.example.aplicaciondecuidadodenios.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListaNinosViewModel : ViewModel() {

    private val _ninos = MutableStateFlow<List<Nino>>(emptyList())
    val ninos: StateFlow<List<Nino>> = _ninos

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun cargarNinos(usuarioId: String) {
        ApiClient.apiService.obtenerNinosPorUsuario(usuarioId)
            .enqueue(object : Callback<List<Nino>> {
                override fun onResponse(call: Call<List<Nino>>, response: Response<List<Nino>>) {
                    if (response.isSuccessful) {
                        _ninos.value = response.body() ?: emptyList()
                    } else {
                        _error.value = "Error al cargar: ${response.code()}"
                    }
                }

                override fun onFailure(call: Call<List<Nino>>, t: Throwable) {
                    _error.value = "Fallo de red: ${t.message}"
                }
            })
    }
}