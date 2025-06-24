package com.example.aplicaciondecuidadodenios.pantallas

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.aplicaciondecuidadodenios.model.ControlCrecimiento
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgregarControlCrecimientoScreen(
    navController: NavController,
    childId: String,
    fechaNacimiento: String // <- Necesaria para calcular edad
) {
    var peso by remember { mutableStateOf("") }
    var talla by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFFFD6D6)),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Nuevo Registro de Crecimiento", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = peso,
            onValueChange = { peso = it },
            label = { Text("Peso (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = talla,
            onValueChange = { talla = it },
            label = { Text("Talla (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = observaciones,
            onValueChange = { observaciones = it },
            label = { Text("Observaciones (opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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

                // Calcular edad en meses
                val edadMeses = calcularEdadEnMeses(fechaNacimiento, fechaActual)

                // Mostrar alerta por IMC
                val alerta = when {
                    imc < 14.0 -> "¡IMC bajo!"
                    imc > 18.0 -> "¡IMC alto!"
                    else -> "IMC normal"
                }
                Toast.makeText(context, alerta, Toast.LENGTH_LONG).show()

                // Crear y enviar el control (aquí puedes guardar o usar la API)
                val control = ControlCrecimiento(
                    child_id = childId,
                    fecha = fechaActual,
                    peso_kg = pesoKg,
                    talla_cm = tallaCm,
                    edad_meses = edadMeses,
                    imc = imc,
                    observaciones = observaciones.ifBlank { null }
                )

                // Aquí deberías llamar a tu API si ya tienes endpoint POST para ControlCrecimiento

                // Por ahora solo mensaje
                Toast.makeText(context, "Registro creado con éxito", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // volver atrás
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
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


