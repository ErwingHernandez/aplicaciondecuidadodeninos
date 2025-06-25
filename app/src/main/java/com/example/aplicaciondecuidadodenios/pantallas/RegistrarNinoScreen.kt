package com.example.aplicaciondecuidadodenios.pantallas

import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.aplicaciondecuidadodenios.model.Nino
import com.example.aplicaciondecuidadodenios.network.ApiClient
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistrarNinoScreen(navController: NavController, usuarioId: String) {
    val context = LocalContext.current

    var primerNombre by remember { mutableStateOf("") }
    var segundoNombre by remember { mutableStateOf("") }
    var primerApellido by remember { mutableStateOf("") }
    var segundoApellido by remember { mutableStateOf("") }

    var fechaNacimientoDisplay by remember { mutableStateOf("Fecha de Nacimiento") } // Fecha para mostrar en la UI (dd/mm/yyyy)
    var fechaNacimientoBackend by remember { mutableStateOf("") } // Fecha en formato para el backend (YYYY-MM-DD)

    var showDatePicker by remember { mutableStateOf(false) }

    var edad by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") } // Mantener el nombre de la variable para el DropdownSelector

    var peso by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }

    var identificacion by remember { mutableStateOf("") } // Variable de estado para identificación

    val opcionesEdad = (1..12).map { it.toString() }
    val opcionesGenero = listOf("Masculino", "Femenino")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1F1))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registrar un nuevo niño", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        // Nombres
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = primerNombre,
                onValueChange = { primerNombre = it },
                label = { Text("Primer N.") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    disabledBorderColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)


            )
            OutlinedTextField(
                singleLine = true,
                value = segundoNombre,
                onValueChange = { segundoNombre = it },
                label = { Text("Segundo N.") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    disabledBorderColor = Color.White
                ), shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Apellidos
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                singleLine = true,
                value = primerApellido,
                onValueChange = { primerApellido = it },
                label = { Text("Primer A.") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    disabledBorderColor = Color.White
                ), shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                singleLine = true,
                value = segundoApellido,
                onValueChange = { segundoApellido = it },
                label = { Text("Segundo A.") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    disabledBorderColor = Color.White
                ), shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Identificación (Opcional)
        OutlinedTextField(
            singleLine = true,
            value = identificacion,
            onValueChange = { identificacion = it },
            label = { Text("Identificación (Opcional)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                disabledBorderColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color.Black // Color del texto y el icono
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 1.dp, // Ancho del borde
                brush = SolidColor(Color.White) // Color del borde (si quieres que sea blanco como los textfields)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth() // Asegura que el Row ocupe el ancho del botón
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange, // Icono de calendario
                    contentDescription = "Seleccionar fecha de nacimiento"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = fechaNacimientoDisplay,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        if (showDatePicker) {
            val dateState = rememberDatePickerState() // Nuevo estado para el DatePicker de Material3
            val confirmButton = @Composable {
                Button(onClick = {
                    dateState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis) // Convertir millis a Instant
                            .atZone(ZoneId.systemDefault()) // Obtener en la zona horaria del sistema
                            .toLocalDate() // Convertir a LocalDate

                        val formatterDisplay = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        val formatterBackend = DateTimeFormatter.ofPattern("yyyy-MM-dd")// Ocupa yyyy-MM-dd

                        fechaNacimientoDisplay = selectedDate.format(formatterDisplay)
                        fechaNacimientoBackend = selectedDate.format(formatterBackend)
                    }
                    showDatePicker = false // Cerrar el DatePicker
                }) { Text("Aceptar") }
            }
            val dismissButton = @Composable {
                OutlinedButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = confirmButton,
                dismissButton = dismissButton
            ) {
                DatePicker(state = dateState)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Edad y Género desplegables
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Dropdown para Edad
            DropdownSelector(

                label = "Edad",
                opciones = opcionesEdad,
                valorSeleccionado = edad,
                onSeleccionar = { edad = it },
                modifier = Modifier.weight(1f)
            )

            // Dropdown para Género
            DropdownSelector(
                label = "Género",
                opciones = opcionesGenero,
                valorSeleccionado = genero, // Usa la variable 'genero'
                onSeleccionar = { genero = it }, // Actualiza la variable 'genero'
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Peso y Talla
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = peso,
                onValueChange = { peso = it },
                label = { Text("Peso") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    disabledBorderColor = Color.White
                ), shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = talla,
                onValueChange = { talla = it },
                label = { Text("Talla") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    disabledBorderColor = Color.White
                ), shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (primerNombre.isBlank() || primerApellido.isBlank() || fechaNacimientoBackend.isBlank() || edad.isBlank() || genero.isBlank()) {
                    Toast.makeText(context, "Por favor completa todos los campos obligatorios", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val nombreCompleto = listOfNotNull(
                    primerNombre.trim().takeIf { it.isNotBlank() },
                    segundoNombre.trim().takeIf { it.isNotBlank() },
                    primerApellido.trim().takeIf { it.isNotBlank() },
                    segundoApellido.trim().takeIf { it.isNotBlank() }
                ).joinToString(" ")

                val nuevoNino = Nino(

                    nombre = nombreCompleto,
                    fechaNacimiento = fechaNacimientoBackend, // Usa la fecha formateada para el backend
                    sexo = genero,
                    identificacion = identificacion.ifBlank { null }, // Si está vacío, se envía null
                    usuarioId = usuarioId,

                )

                ApiClient.apiService.registrarNino(nuevoNino).enqueue(object : Callback<Nino> {
                    override fun onResponse(call: Call<Nino>, response: Response<Nino>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Niño registrado con éxito", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Toast.makeText(context, "Error al registrar: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                            Log.e("RegistrarNino", "Error: ${response.code()} - $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<Nino>, t: Throwable) {
                        Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_LONG).show()
                        Log.e("RegistrarNino", "Fallo de red", t)
                    }
                })
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB3B3))
        ) {
            Text("Guardar", color = Color.Black)
        }
    }
}

@Composable
fun DropdownSelector(
    label: String,
    opciones: List<String>,
    valorSeleccionado: String,
    onSeleccionar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = valorSeleccionado,
            onValueChange = { /* Este TextField es readOnly, el valor se actualiza via onSeleccionar */ },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown, contentDescription = "Desplegar",
                    Modifier.clickable { expanded = !expanded }) // Icono clicable para alternar
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // También se puede hacer clic en el campo completo
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                disabledBorderColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth() // <-- Asegura que el menú ocupe el ancho del TextField
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = {
                        onSeleccionar(opcion)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun rememberDatePickerDialog(onDateSelected: (String) -> Unit, onDismiss: () -> Unit): @Composable () -> Unit {
    val context = LocalContext.current
    return {
        val calendar = Calendar.getInstance()
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val fecha = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year) // Formato dd/mm/yyyy
                onDateSelected(fecha)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { onDismiss() }
        }.show()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistrarNinoScreenPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
        val navController = rememberNavController()
        RegistrarNinoScreen(
            navController = navController,
            usuarioId = "6859bd0624c87f947ed748ad"
        )
    }
}