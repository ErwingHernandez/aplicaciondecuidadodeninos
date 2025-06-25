package com.example.aplicaciondecuidadodenios.pantallas

import android.icu.util.Calendar
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgregarControlCrecimientoScreen(
    navController: NavController,
    childId: String,
    fechaNacimiento: String
) {
    var peso by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var showDate by remember { mutableStateOf(false) }
    var observaciones by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF1F1))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nuevo Registro de Crecimiento", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(20.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            value = talla,
            onValueChange = { talla = it },
            label = { Text("Talla (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
        }
        Spacer(modifier = Modifier.height(20.dp))

        // Fecha de registro
        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = {},
            label = { Text(
                "Fecha de registro",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
            ) },
            placeholder = { Text("dd/mm/yyyy") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {showDate = true },
            readOnly = true,
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

        if (showDate) {
            val datePicker = rememberDatePickerDialog(onDateSelected = {
                fecha = it
                showDate = false
            }, onDismiss = {
                showDate = false
            })
            datePicker()
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = { Text("Observaciones (opcional)") },
            modifier = Modifier.fillMaxWidth()
                .height(150.dp),
            maxLines = 5,
            singleLine = false,
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

        Button(
            onClick = {
                val pesoKg = peso.toDoubleOrNull()
                val tallaCm = talla.toDoubleOrNull()
                if (pesoKg == null || tallaCm == null || tallaCm == 0.0) {
                    Toast.makeText(context, "Verifica los datos", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val imc = pesoKg / ((tallaCm / 100) * (tallaCm / 100))
                val fechaActual = LocalDate.now().toString()
                val edadMeses = calcularEdadEnMeses(fechaNacimiento, fechaActual)

                val alerta = when {
                    imc < 14.0 -> "¡IMC bajo!"
                    imc > 18.0 -> "¡IMC alto!"
                    else -> "IMC normal"
                }
                Toast.makeText(context, alerta, Toast.LENGTH_LONG).show()

                val control = ControlCrecimiento(
                    child_id = childId,
                    fecha = fechaActual,
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
                            Toast.makeText(context, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ControlCrecimiento>, t: Throwable) {
                        Toast.makeText(context, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
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

// 🔢 Cálculo de edad en meses desde fecha de nacimiento
@RequiresApi(Build.VERSION_CODES.O)
fun calcularEdadEnMeses(fechaNacimiento: String, fechaActual: String): Int {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val nacimiento = LocalDate.parse(fechaNacimiento, formatter)
    val actual = LocalDate.parse(fechaActual, formatter)
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
    val fakeFechaNacimiento = "2022-01-01" // 1 de enero de 2022

    AplicacionDeCuidadoDeNiñosTheme {
        AgregarControlCrecimientoScreen(
            navController = navController,
            childId = fakeChildId,
            fechaNacimiento = fakeFechaNacimiento
        )
    }
}


@Composable
fun rememberDate(onDateSelected: (String) -> Unit, onDismiss: () -> Unit): @Composable () -> Unit {
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

