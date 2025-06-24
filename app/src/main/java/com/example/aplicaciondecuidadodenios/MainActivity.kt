package com.example.aplicaciondecuidadodenios

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import com.example.aplicaciondecuidadodenios.pantallas.*
import com.example.aplicaciondecuidadodenios.data.UserManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf // ¡Necesitarás esto!
import androidx.compose.runtime.setValue // ¡Necesitarás esto!
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicacionDeCuidadoDeNiñosTheme {
              // RecomendacionesScreen()
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    // Recopila el estado de DataStore como State<T> para usar en Compose
    var isLoading by remember { mutableStateOf(true) }
    var isFirstLaunchState by remember { mutableStateOf(true) } // Estado inicial
    var isLoggedInState by remember { mutableStateOf(false) } // Estado inicial

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Espera a que DataStore emita su primer valor real para isFirstLaunch
        isFirstLaunchState = userManager.isFirstLaunch.first()
        // Espera a que DataStore emita su primer valor real para isLoggedIn
        isLoggedInState = userManager.isLoggedIn.first()
        isLoading = false // Marca que la carga ha terminado
        Log.d("AppNavigation", "DataStore loaded: isFirstLaunchState=$isFirstLaunchState, isLoggedInState=$isLoggedInState")
    }


    // Agrega logs aquí para ver los valores
    Log.d("AppNavigation", "Current states: isLoading=$isLoading, isFirstLaunchState=$isFirstLaunchState, isLoggedInState=$isLoggedInState")

    // Determina la pantalla de inicio basada en el estado
    val startDestination = if (isLoading) {
        "splash" // Muestra el splash mientras carga
    } else if (isFirstLaunchState) { // Usa el estado que ya cargó
        "welcome"
    } else if (isLoggedInState) { // Usa el estado que ya cargó
        "homeScreen"
    } else {
        "login"
    }


    NavHost(navController = navController, startDestination = startDestination) {
        composable("splash") {
            Log.d("AppNavigation", "Navigating to SplashScreen")
            SplashScreen()
        }
        composable("welcome") {
            Log.d("AppNavigation", "Navigating to WelcomeScreen")
            WelcomeScreen(navController = navController, userManager = userManager)
        }
        composable("login") {
            Log.d("AppNavigation", "Navigating to LoginScreen")
            LoginScreen(navController = navController, userManager = userManager)
        }
        composable("register") {
            Log.d("AppNavigation", "Navigating to RegisterScreen")
            RegisterScreen(navController = navController, userManager = userManager)
        }
        composable("homeScreen") {

            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    androidx.compose.material3.Text(
                        text = "¡Bienvenido al Home!",
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(16.dp)
                    )
                    //Parte Judith
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            // Aquí iría tu ID real de usuario (por ejemplo desde DataStore si lo guardaste)
                            navController.navigate("listaNinos/684a6da0925ea4cfbc0f2b03")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        Text("Ver Hijos", color = MaterialTheme.colorScheme.primary)
                    }

                    //Parte Judith
                    Button(onClick = {
                        // Lanzar la corrutina dentro del CoroutineScope
                        scope.launch { // <--- ¡AQUÍ CAMBIA! Usa scope.launch
                            userManager.logout() // Llama a la suspend fun
                            // Navegar a Login después de cerrar sesión
                            navController.navigate("login") {
                                popUpTo("homeScreen") { inclusive = true }
                            }
                        }
                    }) {
                        androidx.compose.material3.Text("Cerrar Sesión")
                    }
                }
            }

        }
        /*Parte Judith*/
        composable("listaNinos/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            ListaNinosScreen(navController = navController, usuarioId = usuarioId)
        }

        composable("registrarNino/{usuarioId}") { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
            RegistrarNinoScreen(navController = navController, usuarioId = usuarioId)
        }

        /*Parte Judith*/
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview()  {
    AplicacionDeCuidadoDeNiñosTheme {
        AppNavigation()
    }
}