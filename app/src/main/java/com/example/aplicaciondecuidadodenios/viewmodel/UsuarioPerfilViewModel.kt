package com.example.aplicaciondecuidadodenios.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicaciondecuidadodenios.model.Usuario
import com.example.aplicaciondecuidadodenios.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Call // <-- Importa de retrofit2
import retrofit2.Callback // <-- Importa de retrofit2
import retrofit2.Response



class UsuarioPerfilViewModel : ViewModel() {

    // Variable para almacenar el ID del usuario que se quiere mostrar
    private val _usuarioId = MutableStateFlow<String?>(null)
    val usuarioId: StateFlow<String?> = _usuarioId.asStateFlow()

    // Variable para almacenar los datos del usuario una vez cargados
    // Será null si aún no se ha cargado o si hubo un error.
    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    // Opcional: para notificar errores o mensajes a la UI
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _updateSuccessful = MutableStateFlow<Boolean?>(null)
    val updateSuccessful: StateFlow<Boolean?> = _updateSuccessful.asStateFlow()


     fun cargarDatosUsuario(id: String) {
        _usuario.value = null
        _errorMessage.value = null

        viewModelScope.launch { // Usa viewModelScope para lanzar coroutines
            ApiClient.apiService.obtenerusuarioporid(id).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        response.body()?.let { user ->
                            _usuario.value = user // Actualiza el usuario cargado
                        } ?: run {
                            // En caso de que la respuesta sea exitosa pero el cuerpo esté vacío
                            _errorMessage.value = "No se recibieron datos de usuario."
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _errorMessage.value = "Error: ${response.code()} - ${errorBody ?: "Error desconocido"}"
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    _errorMessage.value = "Error de red: ${t.message ?: "Conexión fallida"}"
                }
            })
        }
    }


    fun actualizarUsuario(
        userId: String,
        nombre: String,
        correo: String,
        telefono: String,
        direccion: String,
        roli: String,
        nuevaContrasena: String? = null // Ya es nullable
    ) {
        Log.d("UsuarioPerfilViewModel", "Intentando actualizar usuario: $userId")
        _errorMessage.value = null
        _updateSuccessful.value = null

        viewModelScope.launch {
            // Obtener el usuario actual para rellenar los campos que no se modifican,
            // especialmente la contraseña si no se está cambiando.
            val usuarioExistente = _usuario.value

            // Determinar qué contraseña enviar:
            // Si nuevaContrasena no está vacía o nula, úsala.
            // De lo contrario, usa la contraseña existente del usuario.
            val finalContrasena = nuevaContrasena.takeIf { !it.isNullOrBlank() }
                ?: usuarioExistente?.contraseña // Aquí es donde se toma la contraseña existente

            // Construir el objeto Usuario con los datos actualizados
            val usuarioActualizado = Usuario(
                _id = userId, // ¡¡IMPORTANTE: Incluir el ID del usuario!!
                nombre = nombre,
                correo = correo,
                contraseña = finalContrasena ?: "", // Asegúrate de que no sea nula al crear el objeto Usuario

                telefono = telefono,
                rol = roli,
                direccion = direccion

            )
            Log.d("UsuarioPerfilViewModel", "Objeto usuarioActualizado construido: $usuarioActualizado")


            // Llamar a la API para actualizar el usuario
            ApiClient.apiService.actualizarusurio(userId, usuarioActualizado).enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        response.body()?.let { updatedUser ->
                            _usuario.value = updatedUser
                            _updateSuccessful.value = true
                            Log.d("UsuarioPerfilViewModel", "Actualización exitosa: $updatedUser")
                        } ?: run {
                            _errorMessage.value = "Actualización exitosa, pero no se recibieron datos de vuelta."
                            _updateSuccessful.value = false
                            Log.w("UsuarioPerfilViewModel", "Actualización exitosa, pero cuerpo de respuesta nulo.")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        _errorMessage.value = "Error al actualizar: ${response.code()} - ${errorBody ?: "Error desconocido"}"
                        _updateSuccessful.value = false
                        Log.e("UsuarioPerfilViewModel", "Error al actualizar usuario: ${response.code()} - $errorBody")
                    }
                }

                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    _errorMessage.value = "Error de red al actualizar: ${t.message ?: "Conexión fallida"}"
                    _updateSuccessful.value = false
                    Log.e("UsuarioPerfilViewModel", "Fallo de red al actualizar usuario", t)
                }
            })
        }
    }
}

