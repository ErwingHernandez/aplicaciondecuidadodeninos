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

@Composable
fun RecomendacionesScreen(viewModel: RecomendacionesViewModel = viewModel()) {
    val recomendacion by viewModel.recomendaciones.collectAsState()


        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(recomendacion) { item ->
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun RecomendacionesBotones() {
    val opcionesMeses = listOf("0-6 meses", "6-9 meses", "9-12 meses")
    val opcionesAnios = listOf("1-2 años")

    var expandedMeses by remember { mutableStateOf(false) }
    var selectedMeses by remember { mutableStateOf(opcionesMeses[0]) }

    var expandedAnios by remember { mutableStateOf(false) }
    var selectedAnios by remember { mutableStateOf(opcionesAnios[0]) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD6D6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Recomendaciones Nutricionales",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown Meses
            ExposedDropdownMenuBox(
                expanded = expandedMeses,
                onExpandedChange = { expandedMeses = !expandedMeses }
            ) {
                TextField(
                    value = selectedMeses,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Meses") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMeses) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedMeses,
                    onDismissRequest = { expandedMeses = false }
                ) {
                    opcionesMeses.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                selectedMeses = opcion
                                expandedMeses = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dropdown Años
            ExposedDropdownMenuBox(
                expanded = expandedAnios,
                onExpandedChange = { expandedAnios = !expandedAnios }
            ) {
                TextField(
                    value = selectedAnios,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Años") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAnios) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedAnios,
                    onDismissRequest = { expandedAnios = false }
                ) {
                    opcionesAnios.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                selectedAnios = opcion
                                expandedAnios = false
                            }
                        )
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
        RecomendacionesBotones()
    }
}