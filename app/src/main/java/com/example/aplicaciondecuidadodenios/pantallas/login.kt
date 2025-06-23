//// app/src/main/java/com/example/aplicaciondecuidadodenios/pantallas/LoginScreen.kt
//package com.example.aplicaciondecuidadodenios.pantallas
//
//import android.util.Log
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
//import com.example.aplicaciondecuidadodenios.R
//import com.example.aplicaciondecuidadodenios.model.LoginRequest
//import com.example.aplicaciondecuidadodenios.model.Usuario
//import com.example.aplicaciondecuidadodenios.network.ApiClient
//import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LoginScreen(navController: NavController) {
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var passwordVisibility by remember { mutableStateOf(false) }
//    var errorMessage by remember { mutableStateOf<String?>(null) } // Para mostrar errores de inicio de sesión
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFFD6D6)) // Un fondo rosa claro de tu tema (o ajustar)
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 32.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Marcador de posición para el icono/logo de tu app
//                contentDescription = "App Logo",
//                modifier = Modifier
//                    .size(120.dp)
//                    .padding(bottom = 32.dp)
//            )
//
//            Text(
//                text = "Iniciar Sesión", //
//                style = MaterialTheme.typography.headlineMedium,
//                color = Color.Black,
//                modifier = Modifier.padding(bottom = 24.dp)
//            )
//
//            OutlinedTextField(
//                value = email,
//                onValueChange = { email = it },
//                label = { Text("Correo electrónico") }, //
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//
//            OutlinedTextField(
//                value = password,
//                onValueChange = { password = it },
//                label = { Text("Contraseña") }, //
//                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//                trailingIcon = {
//                    val image = if (passwordVisibility)
//                        Icons.Filled.Visibility
//                    else Icons.Filled.VisibilityOff
//                    IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
//                        Icon(imageVector = image, contentDescription = "Alternar visibilidad de contraseña")
//                    }
//                },
//                modifier = Modifier.fillMaxWidth(),
//                singleLine = true
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//
//            TextButton(onClick = { /* TODO: Implementar navegación de recuperación de contraseña */ }) {
//                Text("¿Olvidó su contraseña?", color = MaterialTheme.colorScheme.primary) //
//            }
//            Spacer(modifier = Modifier.height(24.dp))
//
//            Button(
//                onClick = {
//                    errorMessage = null // Limpiar errores previos
//                    val loginRequest = LoginRequest(email, password)
//                    ApiClient.apiService.loginUsuario(loginRequest).enqueue(object : Callback<Usuario> {
//                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
//                            if (response.isSuccessful) {
//                                val user = response.body()
//                                // Manejar el inicio de sesión exitoso (ej., guardar token de usuario, navegar a la pantalla de inicio)
//                                Log.d("LoginScreen", "Inicio de sesión exitoso: $user")
//                                navController.navigate("homeScreen") { // Navegar a la pantalla principal de tu app
//                                    popUpTo("login") { inclusive = true } // Limpiar el login del back stack
//                                }
//                            } else {
//                                // Inicio de sesión fallido (ej., credenciales incorrectas)
//                                val errorBody = response.errorBody()?.string()
//                                Log.e("LoginScreen", "Inicio de sesión fallido: ${response.code()} - $errorBody")
//                                errorMessage = "Error al iniciar sesión. Verifique sus credenciales."
//                            }
//                        }
//
//                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
//                            // Error de red o la llamada a la API falló
//                            Log.e("LoginScreen", "Error de red durante el inicio de sesión", t)
//                            errorMessage = "Error de red: ${t.message}"
//                        }
//                    })
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp)
//            ) {
//                Text("Ingresar") //
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//
//            TextButton(onClick = { navController.navigate("register") }) {
//                Text("Crear cuenta", color = MaterialTheme.colorScheme.secondary) //
//            }
//
//            errorMessage?.let {
//                Text(
//                    text = it,
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier.padding(top = 16.dp)
//                )
//            }
//        }
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun LoginScreenPreview() {
//    AplicacionDeCuidadoDeNiñosTheme {
//        LoginScreen(navController = rememberNavController())
//    }
//}