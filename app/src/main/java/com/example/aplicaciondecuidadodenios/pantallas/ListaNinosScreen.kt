package com.example.aplicaciondecuidadodenios.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.aplicaciondecuidadodenios.R
import com.example.aplicaciondecuidadodenios.model.Nino
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import com.example.aplicaciondecuidadodenios.viewmodel.ListaNinosViewModel
import retrofit2.http.Body


@Composable
fun BodyContent(navController: NavController, usuarioId: String, viewModel: ListaNinosViewModel = viewModel()) {
    val listaNinos by viewModel.ninos.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(
        modifier = Modifier
            .background(Color(0xFFFFD6D6))
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Image(
                modifier = Modifier.fillMaxWidth().height(250.dp).padding(16.dp),
                painter = painterResource(id = R.drawable.bebe),
                contentDescription = "Infante"
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (error != null) {
                Text("Error: $error", color = Color.Red)
            }

            ListaDeNinos(listaNinos = listaNinos, navController = navController)
        }
        FilaDeCincoBotones(navController = navController, usuarioId = usuarioId)
    }
}

@Composable
fun ListaDeNinos(
    listaNinos: List<Nino>,
    navController: NavController
) {
    if (listaNinos.isEmpty()) {
        Text(
            text = "No hay hijos registrados",
            color = Color.DarkGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        )
    } else {
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(listaNinos) { nino ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0E0))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Nombre: ${nino.nombre}", style = MaterialTheme.typography.titleMedium)
                        Text("Fecha nacimiento: ${nino.fecha_nacimiento}")
                        Text("Sexo: ${nino.sexo}")
                        Button(
                            onClick = {
                                navController.navigate("agregarControl/${nino._id}/${nino.fecha_nacimiento}")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA3A5))
                        ) {
                            Text("Registrar Nuevos Datos", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun FilaDeCincoBotones(navController: NavController, usuarioId: String) {
    Column(modifier = Modifier.fillMaxSize() .padding(30.dp, 0.dp, 30.dp, 60.dp),
        verticalArrangement = Arrangement.Bottom) {
        val items = listOf(
            NavItem("Inicio", R.drawable.home, "homeScreen"),
            NavItem("Agregar", R.drawable.add, "registrarNino/$usuarioId"),
            NavItem("Gráficas", R.drawable.grafic, "graficas/$usuarioId"),
            NavItem("Recomendaciones", R.drawable.book, "recomendaciones"),
            NavItem("Perfil", R.drawable.perfil, "#")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD6D6))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { navController.navigate(item.ruta) }
                ) {
                    Image(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item.label, style = typography.bodySmall)
                }
            }
        }
    }
}

data class NavItem(val label: String, val icon: Int, val ruta: String) {}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ListaNinosAScreenPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
        val navController = rememberNavController()
        BodyContent(
            navController = navController,
            usuarioId = "6859bd0624c87f947ed748ad"
        )
    }
}

