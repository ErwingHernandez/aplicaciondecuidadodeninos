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
import com.example.aplicaciondecuidadodenios.model.LoginApiResponse


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
                    val loginRequest = LoginRequest(email, password)
                    ApiClient.apiService.loginUsuario(loginRequest).enqueue(object : Callback<LoginApiResponse> {
                        override fun onResponse(call: Call<LoginApiResponse>, response: Response<LoginApiResponse>) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body() // Ahora es de tipo LoginApiResponse

                                // ¡ACCESO AL ID ANIDADO!
                                // userResponse?.usuario?._id
                                if (loginResponse != null && loginResponse.usuario._id != null) {
                                    val userId = loginResponse.usuario._id // <-- ¡Accede al ID del usuario anidado!

                                    Log.d("LoginScreen", "Inicio de sesión exitoso. ID de usuario: $userId")
                                    Log.d("LoginScreen", "Cuerpo completo de la respuesta de API: $loginResponse") // Muestra la respuesta completa

                                    coroutineScope.launch {
                                        userManager.login(userId)
                                    }

                                    Log.d("LoginScreen", "Estado de login guardado. AppNavigation manejará la navegación.")
                                    currentErrorType = LoginErrorType.NONE
                                    currentErrorMessage = null
                                    Toast.makeText(context, "¡Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()
                                } else {
                                    val msg = "Login exitoso desde API, pero datos de usuario (ID) incompletos. Respuesta: $loginResponse"
                                    Log.e("LoginScreen", msg)
                                    currentErrorType = LoginErrorType.UNKNOWN_ERROR
                                    currentErrorMessage = msg
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                val code = response.code()
                                Log.e("LoginScreen", "Error en el login. Código: $code, Cuerpo del error: $errorBody")

                                when (code) {
                                    401 -> {
                                        currentErrorType = LoginErrorType.INVALID_CREDENTIALS
                                        currentErrorMessage = "Credenciales inválidas. Por favor, verifica tu correo y contraseña."
                                    }
                                    404 -> {
                                        currentErrorType = LoginErrorType.USER_NOT_FOUND
                                        currentErrorMessage = "Usuario no encontrado. Registra una nueva cuenta."
                                    }
                                    else -> {
                                        currentErrorType = LoginErrorType.UNKNOWN_ERROR
                                        currentErrorMessage = "Error desconocido al iniciar sesión. Código: $code"
                                    }
                                }
                                Toast.makeText(context, currentErrorMessage, Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginApiResponse>, t: Throwable) { // <-- ¡Cambio aquí!
                            Log.e("LoginScreen", "Error de red durante el inicio de sesión", t)
                            currentErrorType = LoginErrorType.NETWORK_ERROR
                            currentErrorMessage = "Error de red: ${t.message}. Revisa tu conexión a internet."
                            Toast.makeText(context, currentErrorMessage, Toast.LENGTH_LONG).show()
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