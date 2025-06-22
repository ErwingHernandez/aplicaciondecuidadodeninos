package com.example.aplicaciondecuidadodenios.pantallas

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.aplicaciondecuidadodenios.model.Usuario
import com.example.aplicaciondecuidadodenios.network.ApiClient
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf("Padre/Madre") } // Rol por defecto, podría ser un desplegable más tarde
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD6D6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()), // Hacerlo desplazable para pantallas pequeñas
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Registrarse", //
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") }, //
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") }, //
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(imageVector = image, contentDescription = "Alternar visibilidad de contraseña")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (confirmPasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                        Icon(imageVector = image, contentDescription = "Alternar visibilidad de contraseña")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Podrías usar un desplegable o botones de radio para 'rol'
            OutlinedTextField(
                value = rol,
                onValueChange = { rol = it },
                label = { Text("Rol (ej. Padre/Madre)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono (Opcional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección (Opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    errorMessage = null
                    if (password != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden."
                        return@Button
                    }
                    if (email.isBlank() || password.isBlank() || nombre.isBlank()) {
                        errorMessage = "Por favor, complete todos los campos obligatorios."
                        return@Button
                    }

                    val newUser = Usuario(
                        nombre = nombre,
                        correo = email,
                        contraseña = password, // ¡En una aplicación real, esto se debe hashear antes de enviar!
                        rol = rol,
                        telefono = if (telefono.isNotBlank()) telefono else null,
                        direccion = if (direccion.isNotBlank()) direccion else null
                    )

                    ApiClient.apiService.registrarUsuario(newUser).enqueue(object : Callback<Usuario> {
                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                            if (response.isSuccessful) {
                                val registeredUser = response.body()
                                Log.d("RegisterScreen", "Registro exitoso: $registeredUser")
                                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                navController.navigate("login") // Navegar de vuelta al login después de un registro exitoso
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e("RegisterScreen", "Registro fallido: ${response.code()} - $errorBody")
                                errorMessage = "Error al registrar: ${response.message()}"
                                if (response.code() == 409) { // Ejemplo: Conflicto para un usuario existente
                                    errorMessage = "El correo electrónico ya está registrado."
                                }
                            }
                        }

                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
                            Log.e("RegisterScreen", "Error de red durante el registro", t)
                            errorMessage = "Error de red: ${t.message}"
                        }
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Crear cuenta") //
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.popBackStack() }) { // Volver a la pantalla anterior (probablemente Login)
                Text("Ya tengo una cuenta", color = MaterialTheme.colorScheme.secondary)
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
        RegisterScreen(navController = rememberNavController())
    }
}