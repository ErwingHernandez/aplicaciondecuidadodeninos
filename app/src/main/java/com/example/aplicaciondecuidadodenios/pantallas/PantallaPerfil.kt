package com.example.aplicaciondecuidadodenios.pantallas

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import android.content.Context // Esta es la clase base Context de Android


import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.aplicaciondecuidadodenios.R
import com.example.aplicaciondecuidadodenios.data.UserManager
import com.example.aplicaciondecuidadodenios.viewmodel.UsuarioPerfilViewModel
import kotlinx.coroutines.launch

@Composable
fun DatosPerfilScreen(navController: NavController, usuarioId: String, userManager: UserManager, viewModel: UsuarioPerfilViewModel = viewModel()){

    val usuario by viewModel.usuario.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current

    LaunchedEffect(usuarioId) {
        if (usuarioId.isNotBlank()) {
            viewModel.cargarDatosUsuario(usuarioId)
        }

    }

    Box(
        modifier = Modifier
            .background(Color(0xFFFFD6D6))
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                modifier = Modifier.fillMaxWidth().height(170.dp).padding(16.dp),
                painter = painterResource(id = R.drawable.perfil),
                contentDescription = "Perfil"
            )
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                Text("Nombre: ${usuario?.nombre}", style = MaterialTheme.typography.titleLarge)
                Text("Correo: ${usuario?.correo}", style = MaterialTheme.typography.titleLarge)
                Text("Contraseña: ${usuario?.contraseña}", style = MaterialTheme.typography.titleLarge)
                Text("Rol: ${usuario?.rol}", style = MaterialTheme.typography.titleLarge)
                Text("Telefono: ${usuario?.telefono}", style = MaterialTheme.typography.titleLarge)
                Text("Dirección: ${usuario?.direccion}", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(modifier = Modifier.height(100.dp))
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                Button(modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB3B3)),
                    onClick = {
                        navController.navigate("editarPerfil/${usuarioId}")
                    }
                ) {
                    Text("Editar Perfil", color = Color.Black)
                }
                Button(modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB3B3)),
                    onClick = {
                        coroutineScope.launch { // Lanza la coroutine
                            userManager.logout()
                            Toast.makeText(context, "Sesión cerrada.", Toast.LENGTH_SHORT).show()
                            // Navegar a la pantalla de inicio de sesión y borrar el back stack
                            navController.navigate("login") {
                                popUpTo(navController.graph.startDestinationId) { // Borra todo hasta el inicio
                                    inclusive = true
                                }
                            }
                        }
                    }
                ) {
                    Text("Cerrar Sesión", color = Color.Black)
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class) // Necesario para OutlinedTextFieldDefaults.colors
@Composable
fun EditarPerfilScreen(navController: NavController, usuarioId: String, viewModel: UsuarioPerfilViewModel = viewModel()) {

    val context = LocalContext.current
    val scrollState = rememberScrollState() // Para hacer la pantalla scrollable

    // Observar el usuario cargado y los mensajes de error del ViewModel
    val usuario by viewModel.usuario.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val updateSuccessful by viewModel.updateSuccessful.collectAsState()

    // Estados para los campos de edición
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") } // Campo para la nueva contraseña
    var confirmNuevaContrasena by remember { mutableStateOf("") } // Campo para confirmar nueva contraseña

    val roles = listOf("Padre", "Madre")
    var selectedRol by remember { mutableStateOf(roles[0]) } // Asumiendo un valor por defecto
    var expandedRolDropdown by remember { mutableStateOf(false) }

    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }

    // Estado para manejar errores específicos de la UI de edición
    var currentEditError: String? by remember { mutableStateOf(null) }

    // Disparar la carga de datos cuando la pantalla se compose o el usuarioId cambie
    LaunchedEffect(usuarioId) {
        if (usuarioId.isNotBlank()) {
            viewModel.cargarDatosUsuario(usuarioId)
        }
    }

    // Cuando el usuario se carga (cambia de null a un objeto Usuario), inicializa los campos
    LaunchedEffect(usuario) {
        usuario?.let { user ->
            nombre = user.nombre ?: ""
            correo = user.correo ?: ""
            telefono = user.telefono ?: ""
            direccion = user.direccion ?: ""
            selectedRol = user.rol ?: roles[0] // Asigna el rol si existe, sino el por defecto
            nuevaContrasena = "" // Siempre en blanco por seguridad
            confirmNuevaContrasena = "" // Siempre en blanco
        }
    }

    // Mostrar Toast si hay un mensaje de error del ViewModel
    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.errorMessage // Limpiar el mensaje después de mostrarlo
        }
    }

    // Mostrar Toast y navegar si la actualización fue exitosa
    LaunchedEffect(updateSuccessful) {
        updateSuccessful?.let { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Vuelve a la pantalla de perfil después de un éxito
            } else {
                // El mensaje de error ya se muestra a través de `errorMessage`
            }
            viewModel.errorMessage // Limpia el estado después de manejarlo
        }
    }

    // Mostrar Toast para errores de validación local
    LaunchedEffect(currentEditError) {
        currentEditError?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            currentEditError = null // Limpiar el error después de mostrar
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD6D6)) // Fondo rosa
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 40.dp,
                    start = 16.dp,
                    end = 16.dp
                )
                .verticalScroll(scrollState), // Habilitar scroll
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Editar Perfil",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Indicador de carga si el usuario aún no se ha cargado
            if (usuario == null) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Cargando datos del usuario...", color = Color.Black)
            } else {
                // Contenido del formulario de edición cuando el usuario está cargado
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                    ),
                    shape = RoundedCornerShape(12.dp) // Esquinas redondeadas
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Campo para nueva contraseña
                OutlinedTextField(
                    value = nuevaContrasena,
                    onValueChange = { nuevaContrasena = it },
                    label = { Text("Nueva Contraseña (opcional)") },
                    visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                            Icon(imageVector = image, contentDescription = "Alternar visibilidad de contraseña")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Campo para confirmar nueva contraseña (solo si se está estableciendo una nueva)
                if (nuevaContrasena.isNotBlank()) {
                    OutlinedTextField(
                        value = confirmNuevaContrasena,
                        onValueChange = { confirmNuevaContrasena = it },
                        label = { Text("Confirmar Nueva Contraseña") },
                        visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            val image = if (confirmPasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                                Icon(imageVector = image, contentDescription = "Alternar visibilidad de contraseña")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }


                ExposedDropdownMenuBox(
                    expanded = expandedRolDropdown,
                    onExpandedChange = { expandedRolDropdown = !expandedRolDropdown },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedRol,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rol") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRolDropdown) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            unfocusedBorderColor = Color.Black,
                            unfocusedLabelColor = Color.Black,
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedRolDropdown,
                        onDismissRequest = { expandedRolDropdown = false }
                    ) {
                        roles.forEach { rolOption ->
                            DropdownMenuItem(
                                text = { Text(rolOption) },
                                onClick = {
                                    selectedRol = rolOption
                                    expandedRolDropdown = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        // Validaciones locales antes de llamar a la API
                        if (nombre.isBlank() || correo.isBlank() || telefono.isBlank() || direccion.isBlank()) {
                            currentEditError = "Todos los campos obligatorios deben ser rellenados."
                            return@Button
                        }
                        if (nuevaContrasena.isNotBlank() && nuevaContrasena != confirmNuevaContrasena) {
                            currentEditError = "Las nuevas contraseñas no coinciden."
                            return@Button
                        }

                        // Llama a la función de actualización en el ViewModel
                        usuarioId.let { id ->
                            viewModel.actualizarUsuario(
                                userId = id,
                                nombre = nombre,
                                correo = correo,
                                telefono = telefono,
                                direccion = direccion,
                                roli = selectedRol, // Pasa el rol seleccionado
                                nuevaContrasena = nuevaContrasena.takeIf { it.isNotBlank() } // Solo envía si se ha escrito una nueva
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB3B3))
                ) {
                    Text("Guardar Cambios", color = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}






@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DatosPerfilPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
//        val navController = rememberNavController()
//        DatosPerfilScreen(
//            navController= navController,
//            usuarioId = "6859bd0624c87f947ed748ad"
//        )

        val navController = rememberNavController()
        EditarPerfilScreen(navController= navController,
           usuarioId = "6859bd0624c87f947ed748ad")
    }
}