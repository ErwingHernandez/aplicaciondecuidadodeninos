package com.example.aplicaciondecuidadodenios.pantallas


import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.aplicaciondecuidadodenios.model.ControlCrecimiento
import com.example.aplicaciondecuidadodenios.network.ApiClient
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class) // Necesario para rememberDatePickerState y DatePickerDialog
@Composable
fun AgregarControlCrecimientoScreen(
    navController: NavController,
    childId: String,
    fechaNacimiento: String // Esta es la fecha de nacimiento del niño (esperado YYYY-MM-DD)
) {
    var peso by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }
    var fechaRegistroDisplay by remember { mutableStateOf("") } // Fecha para mostrar en la UI (dd/mm/yyyy)
    var fechaRegistroBackend by remember { mutableStateOf("") } // Fecha para enviar al backend (YYYY-MM-DD)
    var showDatePicker by remember { mutableStateOf(false) }
    var observaciones by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Inicializa fechaRegistroDisplay y fechaRegistroBackend con la fecha actual al cargar
    LaunchedEffect(Unit) {
        val today = LocalDate.now()
        val formatterDisplay = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formatterBackend = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        fechaRegistroDisplay = today.format(formatterDisplay)
        fechaRegistroBackend = today.format(formatterBackend)
    }

    Box( modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFD6D6))
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()

                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Nuevo Registro de Crecimiento", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = peso,
                    onValueChange = { newValue ->
                        // Permitir solo números y un punto decimal
                        if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            peso = newValue
                        }
                    },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
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

                OutlinedTextField(
                    value = talla,
                    onValueChange = { newValue ->
                        // Permitir solo números y un punto decimal
                        if (newValue.matches(Regex("^\\d*\\.?\\d*\$"))) {
                            talla = newValue
                        }
                    },
                    label = { Text("Talla (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
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
            }
            Spacer(modifier = Modifier.height(20.dp))


            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = SolidColor(Color.White)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha de registro"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = fechaRegistroDisplay,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (showDatePicker) {
                val dateState = rememberDatePickerState()
                val confirmButton = @Composable {
                    Button(onClick = {
                        dateState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val formatterDisplay = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            val formatterBackend = DateTimeFormatter.ofPattern("yyyy-MM-dd")

                            fechaRegistroDisplay = selectedDate.format(formatterDisplay)
                            fechaRegistroBackend = selectedDate.format(formatterBackend)
                        }
                        showDatePicker = false
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

            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = { Text("Observaciones (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                maxLines = 5,
                singleLine = false,
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

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val pesoKg = peso.toDoubleOrNull()
                    val tallaCm = talla.toDoubleOrNull()

                    if (pesoKg == null || tallaCm == null || tallaCm <= 0.0) { // Talla no puede ser 0 o negativa
                        Toast.makeText(context, "Por favor, ingresa valores válidos para peso y talla.", Toast.LENGTH_LONG).show()
                        return@Button
                    }
                    if (fechaRegistroBackend.isBlank()) {
                        Toast.makeText(context, "Por favor, selecciona una fecha de registro.", Toast.LENGTH_LONG).show()
                        return@Button
                    }


                    val edadMeses = try {
                        calcularEdadEnMeses(fechaNacimiento, fechaRegistroBackend)
                    } catch (e: DateTimeParseException) {
                        Toast.makeText(context, "Error al calcular edad: Formato de fecha de nacimiento o registro inválido.", Toast.LENGTH_LONG).show()
                        Log.e("AgregarControl", "Error parsing date for age calculation: ${e.message}")
                        return@Button
                    }


                    val imc = pesoKg / ((tallaCm / 100) * (tallaCm / 100))
                    val alerta = when {
                        imc < 14.0 -> "¡IMC bajo!"
                        imc > 18.0 -> "¡IMC alto!"
                        else -> "IMC normal"
                    }
                    Toast.makeText(context, alerta, Toast.LENGTH_LONG).show()

                    val control = ControlCrecimiento(
                        // _id se omite porque el backend lo genera
                        child_id = childId,
                        fecha = fechaRegistroBackend, // Usa la fecha de registro formateada para la API
                        peso_kg = pesoKg,
                        talla_cm = tallaCm,
                        edad_meses = edadMeses,
                        imc = imc,
                        observaciones = observaciones.ifBlank { null }
                    )

                    ApiClient.apiService.registrarControl(control).enqueue(object : Callback<ControlCrecimiento> {
                        override fun onResponse(
                            call: Call<ControlCrecimiento>,
                            response: Response<ControlCrecimiento>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Registro creado con éxito", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                val errorBody = response.errorBody()?.string()
                                Toast.makeText(context, "Error: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                                Log.e("AgregarControl", "Error response: ${response.code()} - $errorBody")
                            }
                        }

                        override fun onFailure(call: Call<ControlCrecimiento>, t: Throwable) {
                            Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
                            Log.e("AgregarControl", "Network error: ${t.message}", t)
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


}

// Cálculo de edad en meses desde fecha de nacimiento

@RequiresApi(Build.VERSION_CODES.O)
fun calcularEdadEnMeses(fechaNacimiento: String, fechaActualRegistro: String): Int {


    var nacimiento: LocalDate
    try {

        nacimiento = Instant.parse(fechaNacimiento)
            .atZone(java.time.ZoneId.systemDefault()) // O ZoneOffset.UTC si sabes que es UTC
            .toLocalDate()
    } catch (e: DateTimeParseException) {

        Log.e("calcularEdad", "Failed to parse fechaNacimiento as ISO Instant, trying ISO_LOCAL_DATE: ${fechaNacimiento}", e)
        val formatterISO = DateTimeFormatter.ISO_LOCAL_DATE // Para YYYY-MM-DD
        nacimiento = LocalDate.parse(fechaNacimiento, formatterISO)
    }


    val formatterRegistro = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val actual = LocalDate.parse(fechaActualRegistro, formatterRegistro)

    val periodo = Period.between(nacimiento, actual)
    return periodo.years * 12 + periodo.months
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun PreviewAgregarControlCrecimientoScreen() {
    val navController = rememberNavController()

    // Simulación de datos de prueba
    val fakeChildId = "123456"
    val fakeFechaNacimiento = "2022-01-01" // 1 de enero de 2022, formato YYYY-MM-DD

    AplicacionDeCuidadoDeNiñosTheme {
        AgregarControlCrecimientoScreen(
            navController = navController,
            childId = fakeChildId,
            fechaNacimiento = fakeFechaNacimiento
        )
    }
}