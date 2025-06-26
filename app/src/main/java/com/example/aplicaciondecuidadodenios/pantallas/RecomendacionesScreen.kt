
package com.example.aplicaciondecuidadodenios.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import com.example.aplicaciondecuidadodenios.viewmodel.RecomendacionesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecomendacionesScreen(viewModel: RecomendacionesViewModel = viewModel()) {
    val recomendaciones by viewModel.recomendaciones.collectAsState()

    // Opciones de filtro
    val opciones = listOf(
        "Todos",
        "0-6 meses",
        "6-9 meses",
        "9-12 meses",
        "12-24 meses",
        "25+ meses" // ¡Importante añadir si tienes esta recomendación en tu JSON!
    )

    var expanded by remember { mutableStateOf(false) }
    var seleccion by remember { mutableStateOf("Todos") }

    // Filtro basado en selección
    val recomendacionesFiltradas = when (seleccion) {
        "0-6 meses" -> recomendaciones.filter { item ->
            item.edad_min_meses >= 0 && item.edad_max_meses <= 6
        }
        "6-9 meses" -> recomendaciones.filter { item ->
            // Considera si quieres 6-8 exacto, o hasta 9 si hay ítems que terminan en 9
            item.edad_min_meses >= 6 && item.edad_max_meses <= 9
        }
        "9-12 meses" -> recomendaciones.filter { item ->
            // Considera si quieres 9-11 exacto, o hasta 12
            item.edad_min_meses >= 9 && item.edad_max_meses <= 12
        }
        "12-24 meses" -> recomendaciones.filter { item ->
            item.edad_min_meses >= 12 && item.edad_max_meses <= 24
        }
        "25+ meses" -> recomendaciones.filter { item ->
            item.edad_min_meses >= 25 // Asume que cualquier cosa de 25 en adelante
        }
        else -> recomendaciones // Muestra "Todos"
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
                    end = 16.dp)

        ) {

            // Dropdown para seleccionar filtro
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = seleccion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrar por meses") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    colors =  TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,

                        unfocusedBorderColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                    ),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    opciones.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                seleccion = opcion
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista filtrada de recomendaciones
            LazyColumn {
                items(recomendacionesFiltradas) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Edad: ${item.edad_min_meses} a ${item.edad_max_meses} meses",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.recomendacion)
                        }
                    }
                }
                // Si la lista está vacía, puedes mostrar un mensaje
                if (recomendacionesFiltradas.isEmpty() && seleccion != "Todos") {
                    item {
                        Text(
                            text = "No hay recomendaciones para esta edad.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else if (recomendacionesFiltradas.isEmpty() && seleccion == "Todos") {
                    item {
                        Text(
                            text = "Cargando recomendaciones o no hay disponibles.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }



}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecomendacionesScreenPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
        RecomendacionesScreen()
    }
}
