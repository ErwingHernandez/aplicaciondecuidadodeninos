package com.example.aplicaciondecuidadodenios.pantallas

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.aplicaciondecuidadodenios.R
import com.example.aplicaciondecuidadodenios.model.Nino
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import com.example.aplicaciondecuidadodenios.viewmodel.ListaNinosViewModel
import androidx.compose.runtime.LaunchedEffect
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, usuarioId: String) {
    BodyContent(navController = navController, usuarioId = usuarioId)
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BodyContent(navController: NavController, usuarioId: String, viewModel: ListaNinosViewModel = viewModel()) {
    val listaNinos by viewModel.ninos.collectAsState()
    val error by viewModel.error.collectAsState()


    LaunchedEffect(usuarioId) {

        if (usuarioId.isNotBlank()) {
            viewModel.obtenerNinosPorUsuario(usuarioId)
        }
    }

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

            Spacer(modifier = Modifier.height(16.dp))
            if (error != null) {
                Text("Error: $error", color = Color.Red)
            }

            ListaDeNinos(
                listaNinos = listaNinos,
                navController = navController,
                modifier = Modifier.weight(1f) // Esto permite que la LazyColumn ocupe el espacio disponible
                    .padding(bottom = 90.dp) // <-- ¡CAMBIO CRUCIAL AQUÍ! Añade un padding inferior
            )
        }
        FilaDeCincoBotones(navController = navController, usuarioId = usuarioId)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ListaDeNinos(
    listaNinos: List<Nino>,
    navController: NavController,
    modifier: Modifier
) {
    if (listaNinos.isEmpty()) {
        Text(
            text = "No hay hijos registrados",
            color = Color.DarkGray,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
                .fillMaxWidth()
                .padding(32.dp)
        )
    } else {
        LazyColumn(modifier = modifier.padding(16.dp)) {
            items(listaNinos) { nino ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0E0))
                ) {
                    Image(
                        modifier = Modifier.fillMaxWidth().height(250.dp).padding(16.dp),
                        painter = painterResource(id = R.drawable.bebe),
                        contentDescription = "Infante"
                    )
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Nombre: ${nino.nombre}", style = MaterialTheme.typography.titleMedium)
                        Text("Fecha nacimiento: ${formatDate(nino.fechaNacimiento)}")
                        Text("Sexo: ${nino.sexo}")
                        Button(
                            onClick = {
                                navController.navigate("agregarControl/${nino._id}/${nino.fechaNacimiento}")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA3A5))
                        ) {
                            Text("Registrar Crecimiento", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun FilaDeCincoBotones(navController: NavController, usuarioId: String) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 30.dp, vertical = 0.dp),
        verticalArrangement = Arrangement.Bottom) {
        val items = listOf(
            NavItem("Agregar", R.drawable.add, "registrarNino/$usuarioId"),
            NavItem("Gráficas", R.drawable.grafic, "graficas/$usuarioId"),
            NavItem("Recomendaciones", R.drawable.book, "recomendaciones"),
            NavItem("Perfil", R.drawable.perfil, "perfilScreen/$usuarioId") // Ruta para Perfil
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFD6D6))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            items.forEach { item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { navController.navigate(item.ruta) }
                        .weight(1f)
                        .padding(horizontal = 2.dp)
                ) {
                    Image(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = item.label, style = MaterialTheme.typography.bodySmall,
                        softWrap = true,
                        textAlign = TextAlign.Center,)
                }
            }
        }
    }
}

data class NavItem(val label: String, val icon: Int, val ruta: String) {}


@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(isoDateString: String?): String {
    if (isoDateString.isNullOrBlank()) {
        return "Fecha desconocida"
    }
    return try {
        // Formateador para la entrada (ISO 8601 con o sin zona horaria)
        val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME // Para "2022-12-01T00:00:00.000Z"
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Parsear la fecha y luego formatearla
        val date = LocalDate.parse(isoDateString, inputFormatter)
        date.format(outputFormatter)
    } catch (e: DateTimeParseException) {
        // Si falla el parseo ISO, intenta solo con la parte de la fecha si es "YYYY-MM-DD"
        try {
            val inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE // Para "YYYY-MM-DD"
            val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val date = LocalDate.parse(isoDateString, inputFormatter)
            date.format(outputFormatter)
        } catch (e2: DateTimeParseException) {
            // Si todo falla, devuelve la cadena original o un mensaje de error
            "Fecha inválida: $isoDateString"
        }
    } catch (e: Exception) {
        "Error de formato de fecha: ${e.message}"
    }
}

