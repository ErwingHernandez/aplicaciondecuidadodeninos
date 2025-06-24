package com.example.aplicaciondecuidadodenios.pantallas

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    var nombre by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var identificacion by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD6D6))
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registrar Nuevo Hijo", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre Completo") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = { fechaNacimiento = it },
            label = { Text("Fecha de Nacimiento (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = sexo,
            onValueChange = { sexo = it },
            label = { Text("Sexo (Masculino/Femenino)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = identificacion,
            onValueChange = { identificacion = it },
            label = { Text("Identificación (Opcional)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (nombre.isBlank() || fechaNacimiento.isBlank() || sexo.isBlank()) {
                    Toast.makeText(context, "Completa todos los campos obligatorios", Toast.LENGTH_LONG).show()
                    return@Button
                }

                val nuevoNino = Nino(
                    nombre = nombre,
                    fecha_nacimiento = fechaNacimiento,
                    sexo = sexo,
                    identificacion = identificacion.ifBlank { null },
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
                .height(50.dp)
        ) {
            Text("Guardar Niño")
        }
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
