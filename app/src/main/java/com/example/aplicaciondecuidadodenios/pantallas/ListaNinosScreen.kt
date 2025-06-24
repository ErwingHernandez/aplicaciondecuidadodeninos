package com.example.aplicaciondecuidadodenios.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicaciondecuidadodenios.R
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import com.example.aplicaciondecuidadodenios.viewmodel.ListaNinosViewModel

@Composable
fun ListaNinosScreen(usuarioId: String, viewModel: ListaNinosViewModel = viewModel()) {
    val listaNinos by viewModel.ninos.collectAsState()
    val error by viewModel.error.collectAsState()

    // Llamar al cargar la pantalla
    LaunchedEffect(usuarioId) {
        viewModel.cargarNinos(usuarioId)
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFD6D6))
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Text("Infantes",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Image(
                modifier = Modifier.fillMaxWidth().height(250.dp).padding(16.dp),
                painter = painterResource(id= R.drawable.bebe),
                contentDescription = "Infante")
            Spacer(modifier = Modifier.height(16.dp))

            if (error != null) {
                Text("Error: $error", color = Color.Red)
            }

            LazyColumn (
                modifier = Modifier.fillMaxWidth()
                    .background(Color.Red)
                ){
                items(listaNinos) { nino ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre: ${nino.nombre}", style = MaterialTheme.typography.titleMedium)
                            Text("Fecha nacimiento: ${nino.fecha_nacimiento}")
                            Text("Sexo: ${nino.sexo}")
                        }
                    }
                }
            }
            FilaDeCincoBotones()
        }
    }

}

@Composable
fun FilaDeCincoBotones() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
            Button(
                onClick = { /* Acción del botón */ },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            ) {
                Text("Btn")
            }
        Button(
            onClick = { /* Acción del botón */ },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            Text("Btn")
        }
        Button(
            onClick = { /* Acción del botón */ },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            Text("Btn")
        }
        Button(
            onClick = { /* Acción del botón */ },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            Text("Btn")
        }
        Button(
            onClick = { /* Acción del botón */ },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            Text("Btn")
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListaNinosAScreenPreview(){
    AplicacionDeCuidadoDeNiñosTheme {
        ListaNinosScreen(usuarioId = "")
    }
}