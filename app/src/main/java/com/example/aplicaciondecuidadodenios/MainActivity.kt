package com.example.aplicaciondecuidadodenios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicaciondecuidadodenios.pantallas.LoginScreen
import com.example.aplicaciondecuidadodenios.pantallas.RecomendacionesScreen
import com.example.aplicaciondecuidadodenios.pantallas.RegisterScreen
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme


// app/src/main/java/com/example/aplicaciondecuidadodenios/MainActivity.kt

import com.example.aplicaciondecuidadodenios.pantallas.* // Importa tus nuevas pantallas

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionDeCuidadoDeNiñosTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "welcome") {
                    composable("welcome") {
                        WelcomeScreen(navController = navController)
                    }
                    composable("login") {
                        LoginScreen(navController = navController)
                    }
                    composable("register") {
                        RegisterScreen(navController = navController)
                    }
                    // Añade tu pantalla principal autenticada aquí (ej., HomeScreen o RecomendacionesScreen)
                    composable("homeScreen") {
                        RecomendacionesScreen() // Aquí es donde los usuarios van después de un inicio de sesión exitoso
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
        // Puedes previsualizar una pantalla específica o el host de navegación si es necesario
        // Para una previsualización completa, considera usar múltiples anotaciones @Preview
        // o una función Composable dedicada para la previsualización del grafo de navegación.
        // Por ahora, previsualicemos la WelcomeScreen.
        // WelcomeScreen(navController = rememberNavController()) // Ejemplo
    }
}