package com.example.aplicaciondecuidadodenios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
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
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import com.example.aplicaciondecuidadodenios.model.*
import com.example.aplicaciondecuidadodenios.network.*
import com.example.aplicaciondecuidadodenios.viewmodel.RecomendacionesViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionDeCuidadoDeNiñosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

/*Area de trabajo Karenia*/




/*Area de trabajo Judith*/




/*Area de trabajo Erwing*/

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
        "12-24 meses"
    )

    var expanded by remember { mutableStateOf(false) }
    var seleccion by remember { mutableStateOf("Todos") }

    // Filtro basado en selección
    val recomendacionesFiltradas = when (seleccion) {
        "0-6 meses" -> recomendaciones.filter { it.edad_meses_min >= 0 && it.edad_meses_max <= 6 }
        "6-9 meses" -> recomendaciones.filter { it.edad_meses_min >= 6 && it.edad_meses_max <= 9 }
        "9-12 meses" -> recomendaciones.filter { it.edad_meses_min >= 9 && it.edad_meses_max <= 12 }
        "12-24 meses" -> recomendaciones.filter { it.edad_meses_min >= 12 && it.edad_meses_max <= 24}
        else -> recomendaciones
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD6D6))
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            // Dropdown para seleccionar filtro
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = seleccion,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtrar por edad") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
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
                                text = "Edad: ${item.edad_meses_min} a ${item.edad_meses_max} meses",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = item.contenido)
                        }
                    }
                }
            }
        }
    }


}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview()  {
    AplicacionDeCuidadoDeNiñosTheme {
        RecomendacionesScreen()
    }
}