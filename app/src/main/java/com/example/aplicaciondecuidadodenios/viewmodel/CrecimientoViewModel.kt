package com.example.aplicaciondecuidadodenios.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.aplicaciondecuidadodenios.model.*
import com.example.aplicaciondecuidadodenios.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Nivel de importaciones para el ViewModel

// Importaciones necesarias para StateFlow.map, combine, viewModelScope y SharingStarted
import androidx.lifecycle.viewModelScope // Para viewModelScope
import kotlinx.coroutines.flow.map // Para la función .map de StateFlow
import kotlinx.coroutines.flow.combine // Para la función .combine de StateFlow
import kotlinx.coroutines.flow.stateIn // Para la función .stateIn de StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

data class NinoConControles(
    val nino: Nino,
    val controles: List<ControlCrecimiento>
)

class CrecimientoViewModel : ViewModel() {

    private val _listaNinos = MutableStateFlow<List<Nino>>(emptyList())
    val listaNinos: StateFlow<List<Nino>> = _listaNinos // <-- Asegúrate de tener esta línea para el acceso público

    val nombresNinos: StateFlow<List<String>> = _listaNinos.map { ninos ->
        ninos.map { it.nombre }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _ninoSeleccionadoId = MutableStateFlow<String?>(null)
    val ninoSeleccionadoId: StateFlow<String?> = _ninoSeleccionadoId

    // Nuevo StateFlow para almacenar el NinoConControles del niño seleccionado
    private val _ninoConControlesActual = MutableStateFlow<NinoConControles?>(null)
    val ninoConControlesActual: StateFlow<NinoConControles?> = _ninoConControlesActual

    init {
        // Observa cuando _ninoSeleccionadoId cambia y carga los controles
        _ninoSeleccionadoId
            .filterNotNull() // Asegura que solo se procesen IDs no nulos
            .distinctUntilChanged() // Procesa solo si el ID es diferente al anterior
            .onEach { ninoId ->
                // Busca el niño en la lista actual
                val nino = _listaNinos.value.find { it._id == ninoId }
                if (nino != null) {
                    cargarControlesParaUnSoloNino(nino) // Llama a la carga
                } else {
                    _ninoConControlesActual.value = null // Resetea si el niño no se encuentra
                }
            }
            .launchIn(viewModelScope) // Lanza esta colecta en el scope del ViewModel
        // Nota: Para launchIn, necesitas import kotlinx.coroutines.flow.launchIn
        // o puedes usar .collect { /* llamar a la carga */ } dentro de un launch individual
    }


    // Función para seleccionar un niño
    fun seleccionarNino(ninoId: String) {
        _ninoSeleccionadoId.value = ninoId
    }

    // Función para cargar todos los niños (se llama una vez al inicio)
    fun cargarTodosLosNinos(idUsuario: String) {
        Log.d("CrecimientoVM", "Cargando todos los niños para usuario $idUsuario...")

        ApiClient.apiService.obtenerNinosPorUsuario(idUsuario)
            .enqueue(object : Callback<List<Nino>> {
                override fun onResponse(call: Call<List<Nino>>, response: Response<List<Nino>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val ninos = response.body()!!
                        _listaNinos.value = ninos
                        Log.d("CrecimientoVM", "Niños obtenidos: ${ninos.size}")

                        // Selecciona el primer niño por defecto si no hay ninguno seleccionado
                        // Y si no hay un niño ya seleccionado
                        if (_ninoSeleccionadoId.value == null && ninos.isNotEmpty()) {
                            _ninoSeleccionadoId.value = ninos.first()._id
                        } else if (_ninoSeleccionadoId.value != null && ninos.isNotEmpty()) {
                            // Si ya había un niño seleccionado, asegúrate de que todavía existe en la lista
                            // y si no, selecciona el primero
                            if (ninos.none { it._id == _ninoSeleccionadoId.value }) {
                                _ninoSeleccionadoId.value = ninos.first()._id
                            }
                        }
                    } else {
                        Log.e("CrecimientoVM", "Error obteniendo niños: ${response.code()} - ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<List<Nino>>, t: Throwable) {
                    Log.e("CrecimientoVM", "Fallo al obtener niños: ${t.message}", t)
                }
            })
    }

    // Función privada para cargar los controles de un solo niño
    private fun cargarControlesParaUnSoloNino(nino: Nino) {
        Log.d("CrecimientoVM", "Cargando controles para el niño: ${nino.nombre}")
        ApiClient.apiService.obtenerControlesPorNino(nino._id ?: "")
            .enqueue(object : Callback<List<ControlCrecimiento>> {
                override fun onResponse(call: Call<List<ControlCrecimiento>>, response: Response<List<ControlCrecimiento>>) {
                    if (response.isSuccessful) {
                        val controles = response.body() ?: emptyList()
                        // Actualiza el StateFlow _ninoConControlesActual con los datos cargados
                        _ninoConControlesActual.value = NinoConControles(nino, controles)
                        Log.d("CrecimientoVM", "Controles cargados para ${nino.nombre}: ${controles.size}")
                    } else {
                        Log.w("CrecimientoVM", "No se pudieron obtener controles para ${nino.nombre}: ${response.code()} - ${response.message()}")
                        _ninoConControlesActual.value = NinoConControles(nino, emptyList())
                    }
                }

                override fun onFailure(call: Call<List<ControlCrecimiento>>, t: Throwable) {
                    Log.e("CrecimientoVM", "Error al obtener controles para ${nino.nombre}: ${t.message}", t)
                    _ninoConControlesActual.value = NinoConControles(nino, emptyList())
                }
            })
    }


}