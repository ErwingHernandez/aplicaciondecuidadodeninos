package com.example.aplicaciondecuidadodenios.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicaciondecuidadodenios.R
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme

@Composable
fun SplashScreen() {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFFFFD6D6))){
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img), // Tu logo o imagen de splash
                contentDescription = "App Logo",
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = "Cargando...",
                fontSize = 20.sp,
                color = Color.White, // Ajusta el color del texto
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
        SplashScreen()
    }
}