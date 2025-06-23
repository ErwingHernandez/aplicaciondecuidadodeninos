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
import com.example.aplicaciondecuidadodenios.model.Usuario
import com.example.aplicaciondecuidadodenios.network.ApiClient
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.aplicaciondecuidadodenios.data.UserManager
import kotlinx.coroutines.launch

enum class FieldErrorType {
    NONE,
    PASSWORD_MISMATCH,
    REQUIRED_FIELDS,
    EMAIL_EXISTS,
    NETWORK_ERROR,
    UNKNOWN_ERROR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, userManager: UserManager) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val roles = listOf("Padre", "Madre")
    var selectedRol by remember { mutableStateOf(roles[0]) }
    var expanded by remember { mutableStateOf(false) }

    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }
    // --- CAMBIOS PARA EL MANEJO DE ERRORES Y SCROLL ---
    var currentErrorType by remember { mutableStateOf(FieldErrorType.NONE) }
    var currentErrorMessage by remember { mutableStateOf<String?>(null) } // Mensaje para el Toast
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope() // Necesitas un CoroutineScope para lanzar scrolls
    val focusManager = LocalFocusManager.current // Para manejar el foco
    val context = LocalContext.current

    // FocusRequesters para cada campo que pueda tener un error directo
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    val nombreFocusRequester = remember { FocusRequester() }

    LaunchedEffect(currentErrorType) {
        if (currentErrorType != FieldErrorType.NONE) {
            currentErrorMessage?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show() // Mostrar el Toast
            }

            // Desplazar y enfocar el campo correspondiente
            coroutineScope.launch {
                when (currentErrorType) {
                    FieldErrorType.PASSWORD_MISMATCH -> {
                        confirmPasswordFocusRequester.requestFocus()
                        // Puedes ajustar el scroll si confirmPassword no está visible
                        // Por ejemplo, para que esté en la parte superior de la pantalla visible
                        // val position = confirmPasswordFocusRequester.getViewPosition() // Esto requeriría más lógica de layout
                        // scrollState.scrollTo(position)
                    }
                    FieldErrorType.REQUIRED_FIELDS -> {
                        // En un caso de campos requeridos, el foco iría al primer campo vacío
                        // Aquí solo lo ponemos en nombre como ejemplo. Podrías hacer una verificación más inteligente.
                        if (nombre.isBlank()) nombreFocusRequester.requestFocus()
                        else if (email.isBlank()) emailFocusRequester.requestFocus()
                        else if (password.isBlank()) passwordFocusRequester.requestFocus()
                        else if (confirmPassword.isBlank()) confirmPasswordFocusRequester.requestFocus()
                    }
                    FieldErrorType.EMAIL_EXISTS -> {
                        emailFocusRequester.requestFocus()
                    }
                    else -> Unit // No hay un campo específico para otros errores como red
                }
            }
            // Resetear el tipo de error después de procesarlo para evitar re-triggers accidentales
            currentErrorType = FieldErrorType.NONE
            currentErrorMessage = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD6D6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 40.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(scrollState), // Usar el scrollState aquí
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Registrarse",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(nombreFocusRequester), // Asocia el FocusRequester
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocusRequester), // Asocia el FocusRequester
                singleLine = true
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
                    .focusRequester(passwordFocusRequester), // Asocia el FocusRequester
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
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(confirmPasswordFocusRequester), // Asocia el FocusRequester
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))


            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedRol,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    roles.forEach { rolOption ->
                        DropdownMenuItem(
                            text = { Text(rolOption) },
                            onClick = {
                                selectedRol = rolOption
                                expanded = false
                            }
                        )
                    }
                }
            }
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
                    currentErrorType = FieldErrorType.NONE // Resetear el error
                    currentErrorMessage = null // Resetear el mensaje

                    if (password != confirmPassword) {
                        currentErrorType = FieldErrorType.PASSWORD_MISMATCH
                        currentErrorMessage = "Las contraseñas no coinciden."
                        return@Button
                    }
                    // Mejorado para verificar campos obligatorios uno por uno si necesitas foco específico
                    if (nombre.isBlank()) {
                        currentErrorType = FieldErrorType.REQUIRED_FIELDS
                        currentErrorMessage = "El nombre es obligatorio."
                        return@Button
                    }
                    if (email.isBlank()) {
                        currentErrorType = FieldErrorType.REQUIRED_FIELDS
                        currentErrorMessage = "El correo electrónico es obligatorio."
                        return@Button
                    }
                    if (password.isBlank()) {
                        currentErrorType = FieldErrorType.REQUIRED_FIELDS
                        currentErrorMessage = "La contraseña es obligatoria."
                        return@Button
                    }
                    // Si todos los campos requeridos básicos están bien, procede con la llamada a la API
                    // No es necesario validar selectedRol.isBlank() ya que el dropdown siempre tiene un valor por defecto.


                    val newUser = Usuario(
                        nombre = nombre,
                        correo = email,
                        contraseña = password,
                        rol = selectedRol,
                        telefono = telefono.ifBlank { null },
                        direccion = direccion.ifBlank { null }
                    )

                    ApiClient.apiService.registrarUsuario(newUser).enqueue(object : Callback<Usuario> {
                        override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                            if (response.isSuccessful) {
                                val registeredUser = response.body()
                                Log.d("RegisterScreen", "Registro exitoso: $registeredUser")
                                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                navController.navigate("login")
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Log.e("RegisterScreen", "Registro fallido: ${response.code()} - $errorBody")

                                if (response.code() == 409) {
                                    currentErrorType = FieldErrorType.EMAIL_EXISTS
                                    currentErrorMessage = "El correo electrónico ya está registrado."
                                } else {
                                    currentErrorType = FieldErrorType.UNKNOWN_ERROR
                                    currentErrorMessage = "Error al registrar: ${response.message()}"
                                }
                            }
                        }

                        override fun onFailure(call: Call<Usuario>, t: Throwable) {
                            Log.e("RegisterScreen", "Error de red durante el registro", t)
                            currentErrorType = FieldErrorType.NETWORK_ERROR
                            currentErrorMessage = "Error de red: ${t.message}"
                        }
                    })
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Crear cuenta")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.popBackStack() }) {
                Text("Ya tengo una cuenta", color = MaterialTheme.colorScheme.secondary)
            }


        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
        //RegisterScreen(navController = rememberNavController())
    }
}