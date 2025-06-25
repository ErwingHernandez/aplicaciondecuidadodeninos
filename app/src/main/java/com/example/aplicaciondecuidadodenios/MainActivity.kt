package com.example.aplicaciondecuidadodenios

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.annotation.RequiresApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme
import com.example.aplicaciondecuidadodenios.pantallas.*
import com.example.aplicaciondecuidadodenios.data.UserManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.first

import com.example.aplicaciondecuidadodenios.pantallas.AgregarControlCrecimientoScreen




class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val userManager = remember { UserManager(context) }

    // Estados para la carga inicial y la decisión de la ruta de inicio
    var isReadyToNavigate by remember { mutableStateOf(false) }
    var initialRoute by remember { mutableStateOf<String?>(null) } // Null hasta que se decida la ruta

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        // Observa los cambios en isLoggedIn y userId para reaccionar a los logins/logouts
        // NOTA: userManager.isLoggedIn ahora es un Flow<Boolean>
        // Y userManager.userId ahora será un Flow<String?>
        userManager.isLoggedIn.collect { loggedIn ->
            val userId = userManager.userId.first() // Obtener el ID más reciente
            Log.d("AppNavigation", "isLoggedIn change detected: $loggedIn, userId: $userId")

            if (loggedIn && userId != null) {
                initialRoute = "homeScreen/$userId"
                Log.d("AppNavigation", "Usuario logueado: Navegando a homeScreen/$userId")
            } else {
                // Si no está logueado o el ID es nulo, verifica si es el primer lanzamiento
                val firstLaunch = userManager.isFirstLaunch.first()
                if (firstLaunch) {
                    initialRoute = "welcome"
                    Log.d("AppNavigation", "Primer lanzamiento: Navegando a welcome")
                } else {
                    initialRoute = "login"
                    Log.d("AppNavigation", "No logueado: Navegando a login")
                }
            }
            isReadyToNavigate = true // Marca que la lógica inicial de navegación está lista
        }
    }

    // Muestra un SplashScreen o indicador de carga mientras se decide la ruta inicial
    if (!isReadyToNavigate || initialRoute == null) {
        SplashScreen() // Puedes usar un indicador de carga más simple aquí si quieres, o directamente un CircularProgressIndicator
    } else {
        NavHost(navController = navController, startDestination = initialRoute!!) {
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

            composable(
                route = "homeScreen/{usuarioId}",
                arguments = listOf(navArgument("usuarioId") { type = NavType.StringType })
            ) { backStackEntry ->
                val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
                Log.d("AppNavigation", "Navigating to HomeScreen with usuarioId: $usuarioId")
                HomeScreen(navController = navController, usuarioId = usuarioId)
            }

            composable("registrarNino/{usuarioId}") { backStackEntry ->
                val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
                RegistrarNinoScreen(navController = navController, usuarioId = usuarioId)
            }

            composable("recomendaciones") {
                RecomendacionesScreen()
            }

            composable("graficas/{usuarioId}") { backStackEntry ->
                val usuarioId = backStackEntry.arguments?.getString("usuarioId") ?: ""
                ControlCrecimientoScreen(usuarioId = usuarioId)
            }

            // AHORA LLAMAMOS AL COMPOSABLE CORRECTO: AgregarControlCrecimientoScreen
            composable(
                route = "agregarControl/{ninoId}/{fechaNacimiento}",
                arguments = listOf(
                    navArgument("ninoId") { type = NavType.StringType },
                    navArgument("fechaNacimiento") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val ninoId = backStackEntry.arguments?.getString("ninoId") ?: ""
                val fechaNacimiento = backStackEntry.arguments?.getString("fechaNacimiento") ?: ""
                AgregarControlCrecimientoScreen(
                    navController = navController,
                    childId = ninoId, // Pasamos ninoId como childId
                    fechaNacimiento = fechaNacimiento
                )
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview()  {
    AplicacionDeCuidadoDeNiñosTheme {
        AppNavigation()
    }
}