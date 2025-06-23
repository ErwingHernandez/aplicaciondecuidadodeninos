// app/src/main/java/com/example/aplicaciondecuidadodenios/WelcomeScreen.kt
package com.example.aplicaciondecuidadodenios

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.*
import com.example.aplicaciondecuidadodenios.data.UserManager
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun WelcomeScreen(navController: NavController, userManager: UserManager) {

    val scope = rememberCoroutineScope()// Añadir parámetro NavController
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFD6D6))) {

        Image(
            painter = painterResource(id = R.drawable.img), // Placeholder para tu imagen
            contentDescription = "Ilustración",
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .alpha(0.5f) // Ajustar transparencia si es necesario
        )

        // Contenido centrado
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center, // Centrar verticalmente
            modifier = Modifier
                .fillMaxSize() // Ocupar el tamaño completo para centrar
                .padding(16.dp)
        ) {
            Text(
                text = "Creciendo juntos con salud", //
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "\"Acompañamos a tu hijo desde el primer suspiro hasta su independencia.\"", //
                fontSize = 16.sp,
                color = Color.DarkGray, // Cambiado a DarkGray para una mejor visibilidad
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center // Centrar texto
            )
            Button(
                onClick = {
                    scope.launch {
                        userManager.setFirstLaunchDone() // Esto se llama CUANDO se presiona el botón
                    }
                    navController.navigate("login") {
                        popUpTo("welcome") { inclusive = true } // <-- MUY RECOMENDADO: Eliminar Welcome del back stack
                    }
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFFD8BFD8),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
            ) {
                Text(text = "Empezar")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
        // Para la vista previa, pasamos un NavController ficticio
        //WelcomeScreen(navController = rememberNavController())
    }
}

