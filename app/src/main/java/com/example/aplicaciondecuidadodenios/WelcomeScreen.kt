package com.example.aplicaciondecuidadodenios

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme

//navController: NavHostController
@Composable
fun WelcomeScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo (debe estar en res/drawable/madre.png)
        Image(
            painter = painterResource(id = R.drawable.madre),
            contentDescription = "Ilustración",
            modifier = Modifier.align(Alignment.Center)
        )

        // Contenido centrado
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Creciendo juntos con salud",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "\"Acompañamos a tu hijo desde el primer suspiro hasta su independencia.\"",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )
//            Button(
//                onClick = { navController.navigate("login") },
//                colors = ButtonDefaults.filledTonalButtonColors(
//                    containerColor = Color(0xFFD8BFD8),
//                    contentColor = Color.White
//                ),
//                modifier = Modifier.height(50.dp)
//            ) {
//                Text(text = "Empezar")
//            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecomendacionesScreenPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
        WelcomeScreen()
    }
}