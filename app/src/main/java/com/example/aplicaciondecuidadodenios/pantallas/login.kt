// app/src/main/java/com/example/aplicaciondecuidadodenios/pantallas/LoginScreen.kt
package com.example.aplicaciondecuidadodenios.pantallas

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.aplicaciondecuidadodenios.data.UserManager
import com.example.aplicaciondecuidadodenios.model.LoginRequest
import com.example.aplicaciondecuidadodenios.model.Usuario
import com.example.aplicaciondecuidadodenios.network.ApiClient
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.MaterialTheme


enum class LoginErrorType {
    NONE,
    REQUIRED_FIELDS,
    INVALID_CREDENTIALS,
    USER_NOT_FOUND,
    NETWORK_ERROR,
    UNKNOWN_ERROR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, userManager: UserManager) { // <--- ¡Añade userManager aquí!
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var currentErrorType by remember { mutableStateOf(LoginErrorType.NONE) }
    var currentErrorMessage by remember { mutableStateOf<String?>(null) } // Mensaje para el Toast
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // FocusRequesters para cada campo que pueda tener un error directo
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    LaunchedEffect(currentErrorType) {
        if (currentErrorType != LoginErrorType.NONE) {
            currentErrorMessage?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show() // Mostrar el Toast
            }

            // Enfocar el campo correspondiente
            coroutineScope.launch {
                when (currentErrorType) {
                    LoginErrorType.REQUIRED_FIELDS -> {
                        // Enfoca el primer campo vacío que encuentre
                        if (email.isBlank()) emailFocusRequester.requestFocus()
                        else if (password.isBlank()) passwordFocusRequester.requestFocus()
                    }
                    LoginErrorType.INVALID_CREDENTIALS, LoginErrorType.USER_NOT_FOUND -> {
                        // En estos casos, puedes enfocar el campo de contraseña
                        // ya que suele ser el más común para errores de credenciales.
                        passwordFocusRequester.requestFocus()
                    }
                    else -> Unit // No hay un campo específico para errores de red o desconocidos
                }
            }
            // Resetear el tipo de error después de procesarlo para evitar re-triggers accidentales
            currentErrorType = LoginErrorType.NONE
            currentErrorMessage = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD6D6))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp)
        ) {
            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocusRequester), // Asocia el FocusRequester
                singleLine = true,

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,

                    unfocusedBorderColor = Color.Black,
                    unfocusedLabelColor = Color.Black,

                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                        Icon(imageVector = image, contentDescription = "Alternar visibilidad de contraseña")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),

                singleLine = true,

                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = Color.Black,

                    unfocusedTextColor = Color.Black,

                    unfocusedBorderColor = Color.Black,
                    unfocusedLabelColor = Color.Black,

                    unfocusedTrailingIconColor = Color.DarkGray,
                    focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
                    )
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    currentErrorType = LoginErrorType.NONE // Resetear el error
                    currentErrorMessage = null // Resetear el mensaje

                    // --- VALIDACIÓN DE CAMPOS VACÍOS ---
                    if (email.isBlank()) {
                        currentErrorType = LoginErrorType.REQUIRED_FIELDS
                        currentErrorMessage = "El correo electrónico es obligatorio."
                        return@Button
                    }
                    if (password.isBlank()) {
                        currentErrorType = LoginErrorType.REQUIRED_FIELDS
                        currentErrorMessage = "La contraseña es obligatoria."
                        return@Button
                    }
                    // ------------------------------------

                    val loginRequest = LoginRequest(email, password)
                    ApiClient.apiService.loginUsuario(loginRequest).enqueue(object : Callback<Usuario> {
                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                            if (response.isSuccessful) {
                                val user = response.body()
                                Log.d("LoginScreen", "Inicio de sesión exitoso: $user")
                                coroutineScope.launch { // <--- Usa el scope para DataStore
                                    userManager.login() // <--- ¡Marca como logueado!
                                }
                                navController.navigate("homeScreen") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e("LoginScreen", "Inicio de sesión fallido: ${response.code()} - $errorBody")

                                when (response.code()) {
                                    401 -> { // Unauthorized - Credenciales incorrectas
                                        currentErrorType = LoginErrorType.INVALID_CREDENTIALS
                                        currentErrorMessage = "Credenciales incorrectas. Verifique su correo y contraseña."
                                    }
                                    404 -> { // Not Found - Usuario no encontrado (aunque 401 es más común para esto)
                                        currentErrorType = LoginErrorType.USER_NOT_FOUND
                                        currentErrorMessage = "Usuario no encontrado."
                                    }
                                    else -> {
                                        currentErrorType = LoginErrorType.UNKNOWN_ERROR
                                        currentErrorMessage = "Error al iniciar sesión: ${response.message()}."

                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
                            Log.e("LoginScreen", "Error de red durante el inicio de sesión", t)
                            currentErrorType = LoginErrorType.NETWORK_ERROR
                            currentErrorMessage = "Error de red: ${t.message}"
                        }
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Ingresar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("register") }) {
                Text("Crear nueva cuenta", color = MaterialTheme.colorScheme.secondary)
            }

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
       // LoginScreen(navController = rememberNavController())
    }
}