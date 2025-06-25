package com.example.aplicaciondecuidadodenios.pantallas

import android.icu.util.Calendar
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun RegistrarNinoScreen(navController: NavController, usuarioId: String) {
    val context = LocalContext.current

    var primerNombre by remember { mutableStateOf("") }
    var segundoNombre by remember { mutableStateOf("") }
    var primerApellido by remember { mutableStateOf("") }
    var segundoApellido by remember { mutableStateOf("") }

    var fechaNacimiento by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    var edad by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }

    var peso by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }

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
                value = primerNombre,
                onValueChange = { primerNombre = it },
                label = { Text("Primer Nombre") },
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
                value = segundoNombre,
                onValueChange = { segundoNombre = it },
                label = { Text("Segundo Nombre") },
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

        //Apellido
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = primerApellido,
                onValueChange = { primerApellido = it },
                label = { Text("Primer Apellido") },
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
                value = segundoApellido,
                onValueChange = { segundoApellido = it },
                label = { Text("Segundo Apellido") },
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

        // Fecha de nacimiento
        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = {},
            label = { Text("Fecha de nacimiento") },
            placeholder = { Text("dd/mm/yyyy") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            readOnly = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                disabledBorderColor = Color.White
            ), shape = RoundedCornerShape(12.dp)
        )

        if (showDatePicker) {
            val datePicker = rememberDatePickerDialog(onDateSelected = {
                fechaNacimiento = it
                showDatePicker = false
            }, onDismiss = {
                showDatePicker = false
            })
            datePicker()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Edad y Género desplegables
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DropdownSelector(
                label = "Edad",
                opciones = opcionesEdad,
                valorSeleccionado = edad,
                onSeleccionar = { edad = it },
                modifier = Modifier.weight(1f)
            )

            DropdownSelector(
                label = "Género",
                opciones = opcionesGenero,
                valorSeleccionado = genero,
                onSeleccionar = { genero = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        // Peso y Talla con selectores
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = peso,
                    onValueChange = { peso = it },
                    label = { Text("Peso") },
                    modifier = Modifier.fillMaxWidth(),
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

            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = talla,
                    onValueChange = { talla = it },
                    label = { Text("Talla") },
                    modifier = Modifier.fillMaxWidth(),
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
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (primerNombre.isBlank() || primerApellido.isBlank() || fechaNacimiento.isBlank() || edad.isBlank() || genero.isBlank()) {
                    Toast.makeText(context, "Por favor completa todos los campos obligatorios", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val nombreCompleto = "$primerNombre $segundoNombre $primerApellido $segundoApellido".trim()

                val nuevoNino = Nino(
                    nombre = nombreCompleto,
                    fecha_nacimiento = fechaNacimiento,
                    sexo = genero,
                    identificacion = null, // si tenés un campo opcional puedes usarlo
                    usuario_id = usuarioId
                )

                ApiClient.apiService.registrarNino(nuevoNino).enqueue(object : Callback<Nino> {
                    override fun onResponse(call: Call<Nino>, response: Response<Nino>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Niño registrado con éxito", Toast.LENGTH_SHORT).show()
                            navController.popBackStack() // volver a la pantalla anterior
                        } else {
                            Toast.makeText(context, "Error al registrar: ${response.message()}", Toast.LENGTH_LONG).show()
                            Log.e("RegistrarNino", "Error: ${response.code()} - ${response.errorBody()?.string()}")
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
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                disabledBorderColor = Color.White
            ), shape = RoundedCornerShape(12.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
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
                val fecha = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
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


//@Composable
//fun RegistrarNinoScreen(navController: NavController, usuarioId: String) {
//    var nombre by remember { mutableStateOf("") }
//    var fechaNacimiento by remember { mutableStateOf("") }
//    var sexo by remember { mutableStateOf("") }
//    var identificacion by remember { mutableStateOf("") }
//
//    val context = LocalContext.current
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFFFFD6D6))
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Top,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Registrar Nuevo Hijo", style = MaterialTheme.typography.headlineMedium)
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        OutlinedTextField(
//            value = nombre,
//            onValueChange = { nombre = it },
//            label = { Text("Nombre Completo") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        OutlinedTextField(
//            value = fechaNacimiento,
//            onValueChange = { fechaNacimiento = it },
//            label = { Text("Fecha de Nacimiento (YYYY-MM-DD)") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        OutlinedTextField(
//            value = sexo,
//            onValueChange = { sexo = it },
//            label = { Text("Sexo (Masculino/Femenino)") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        OutlinedTextField(
//            value = identificacion,
//            onValueChange = { identificacion = it },
//            label = { Text("Identificación (Opcional)") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Button(
//            onClick = {
//                if (nombre.isBlank() || fechaNacimiento.isBlank() || sexo.isBlank()) {
//                    Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_LONG).show()
//                    return@Button
//                }
//
//                val nuevoNino = Nino(
//                    nombre = nombre,
//                    fecha_nacimiento = fechaNacimiento,
//                    sexo = sexo,
//                    identificacion = identificacion.ifBlank { null },
//                    usuario_id = usuarioId
//                )
//
//                ApiClient.apiService.registrarNino(nuevoNino).enqueue(object : Callback<Nino> {
//                    override fun onResponse(call: Call<Nino>, response: Response<Nino>) {
//                        if (response.isSuccessful) {
//                            Toast.makeText(context, "Niño registrado con éxito", Toast.LENGTH_SHORT).show()
//                            navController.popBackStack() // volver a la pantalla anterior
//                        } else {
//                            Toast.makeText(context, "Error al registrar: ${response.message()}", Toast.LENGTH_LONG).show()
//                            Log.e("RegistrarNino", "Error: ${response.code()} - ${response.errorBody()?.string()}")
//                        }
//                    }
//
//                    override fun onFailure(call: Call<Nino>, t: Throwable) {
//                        Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_LONG).show()
//                        Log.e("RegistrarNino", "Fallo de red", t)
//                    }
//                })
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//        ) {
//            Text("Guardar Niño")
//        }
//    }
//}
//
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun RegistrarNinoScreenPreview() {
//    AplicacionDeCuidadoDeNiñosTheme {
//        val navController = rememberNavController()
//        RegistrarNinoScreen(
//            navController = navController,
//            usuarioId = "6859bd0624c87f947ed748ad"
//        )
//    }
//}
