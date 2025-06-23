package com.example.aplicaciondecuidadodenios.pantallas

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.aplicaciondecuidadodenios.ui.theme.AplicacionDeCuidadoDeNiñosTheme

import android.widget.LinearLayout // Para LinearLayout.LayoutParams

// Importa el Color de Android tradicional con un alias para evitar conflictos
import android.graphics.Color as AndroidColor // <-- ¡Aquí está el truco!

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicaciondecuidadodenios.viewmodel.CrecimientoViewModel
import com.example.aplicaciondecuidadodenios.viewmodel.NinoConControles
import com.github.mikephil.charting.components.XAxis
import androidx.compose.ui.viewinterop.AndroidView

import com.example.aplicaciondecuidadodenios.model.ControlCrecimiento
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

// Clase de ayuda para formatear etiquetas del eje X a enteros si son meses o decimales si son años
class MyXAxisValueFormatter(private val isMonths: Boolean) : ValueFormatter() {
    @SuppressLint("DefaultLocale")
    override fun getFormattedValue(value: Float): String {
        return if (isMonths) {
            value.toInt().toString() // Mostrar meses como enteros
        } else {
            String.format("%.1f", value) // Mostrar años con un decimal
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class) // Anotación necesaria si usas ciertos componentes de Material3
@Composable
fun ControlCrecimientoScreen(
    usuarioId: String,
    viewModel: CrecimientoViewModel = viewModel()
) {
    // Cargar todos los niños una vez al inicio de la pantalla
    LaunchedEffect(usuarioId) {
        viewModel.cargarTodosLosNinos(usuarioId)
    }

    val nombresNinos by viewModel.nombresNinos.collectAsState()
    val ninoSeleccionadoConControles by viewModel.ninoConControlesActual.collectAsState() // Esta ahora contendrá solo el niño seleccionado

    // Estado para el DropdownMenu
    val todosLosNinos by viewModel.listaNinos.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    // Ya es un solo objeto NinoConControles?, no una lista, por eso se quita .firstOrNull()
    val selectedNinoNombre = ninoSeleccionadoConControles?.nino?.nombre ?: "Seleccionar Niño"


    // NO PONGAS EL GRÁFICO AQUÍ FUERA DEL BOX/COLUMN
    // val currentNino = ninoSeleccionadoConControles // Esto no se va a renderizar aquí


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFD6D6))
    )
    {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp,
                start = 16.dp,
                end = 16.dp)) {

            // Selector de niño (DropdownMenu)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedNinoNombre,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    nombresNinos.forEach { nombreNino ->
                        DropdownMenuItem(
                            text = { Text(nombreNino) },
                            onClick = {
                                val selectedNino = todosLosNinos.find { it.nombre == nombreNino }
                                selectedNino?._id?.let {
                                    viewModel.seleccionarNino(it)
                                }
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Filtro por Mes/Año (Tu filtro existente)
            var filtroSeleccionado by remember { mutableStateOf("meses") } // Decláralo aquí dentro de la Composable para que su estado sea "recordado"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = { filtroSeleccionado = "meses" }) {
                    Text("Por Mes")
                }
                Button(onClick = { filtroSeleccionado = "años" }) {
                    Text("Por Año")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ESTE ES EL LUGAR CORRECTO PARA EL GRÁFICO
            // Y usa directamente 'ninoSeleccionadoConControles' (que es NinoConControles?)
            val currentNinoForChart = ninoSeleccionadoConControles
            if (currentNinoForChart != null && currentNinoForChart.controles.isNotEmpty()) {
                IMCBarChart(currentNinoForChart.controles, filtroSeleccionado)
            } else if (currentNinoForChart != null && currentNinoForChart.controles.isEmpty()) {
                Text("No hay datos de crecimiento para ${currentNinoForChart.nino.nombre}.", modifier = Modifier.padding(16.dp))
            } else {
                Text("Selecciona un niño para ver sus datos de crecimiento.", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun NinoGraficoSection(ninoConControles: NinoConControles, filtroSeleccionado: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Niño: ${ninoConControles.nino.nombre}",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        IMCBarChart(controlCrecimiento = ninoConControles.controles, filtroSeleccionado = filtroSeleccionado)
    }
}

@Composable
fun IMCBarChart(controlCrecimiento: List<ControlCrecimiento>, filtroSeleccionado: String) {
    val context = LocalContext.current

    if (controlCrecimiento.isEmpty()) {
        Text(
            text = "No hay datos de crecimiento disponibles para este niño.",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    AndroidView(
        factory = {
            BarChart(context).apply { // CAMBIO CLAVE: Usamos BarChart
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                axisRight.isEnabled = false // Generalmente no necesitas el eje Y derecho para barras simples
                legend.isEnabled = true
                animateY(1000) // Animación en el eje Y es común para barras
                setNoDataText("Cargando datos...")
                setDrawGridBackground(false) // No dibujar el fondo de la cuadrícula
                description.isEnabled = false // Deshabilitar la descripción por defecto
            }
        },
        update = { chart ->
            // Ordenar los controles por edad_meses para que las barras aparezcan en orden cronológico
            val sortedControls = controlCrecimiento.sortedBy { it.edad_meses }

            val entries = sortedControls.mapNotNull { control ->
                if (control.imc != null && control.edad_meses != null) {
                    val xValue = if (filtroSeleccionado == "meses") {
                        control.edad_meses.toFloat()
                    } else {
                        control.edad_meses.toFloat() / 12f
                    }
                    // Usamos BarEntry. xValue es la posición en el eje X, yValue es la altura de la barra.
                    BarEntry(xValue, control.imc.toFloat()) // CAMBIO CLAVE: Usamos BarEntry
                } else {
                    null
                }
            }

            if (entries.isEmpty()) {
                chart.clear()
                chart.invalidate()
                return@AndroidView
            }

            val dataSet = BarDataSet(entries, "IMC").apply { // CAMBIO CLAVE: Usamos BarDataSet
                color = AndroidColor.BLUE       // Color de las barras
                valueTextColor = AndroidColor.BLACK // Color del texto del valor en la barra
                valueTextSize = 10f // Tamaño del texto del valor
            }

            val barData = BarData(dataSet) // CAMBIO CLAVE: Usamos BarData
            chart.data = barData

            // Ajustar el eje X para barras
            chart.xAxis.apply {
                granularity = 1f // Asegura que las etiquetas del eje X sean enteras (para meses)
                isGranularityEnabled = true
                valueFormatter = MyXAxisValueFormatter(filtroSeleccionado == "meses") // Reutilizamos tu formatter
            }

            // Ajustar el tamaño de las barras y el espaciado si es necesario
            barData.barWidth = 0.5f // Ancho de las barras (0.5 significa el 50% del espacio disponible por "unidad")

            chart.description.text = if (filtroSeleccionado == "meses") "IMC vs Edad (meses)" else "IMC vs Edad (años)"
            chart.notifyDataSetChanged()
            chart.invalidate() // Refrescar el gráfico
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ControlCrecimientoScreenPreview() {
    AplicacionDeCuidadoDeNiñosTheme {
        ControlCrecimientoScreen(usuarioId = "684a6da0925ea4cfbc0f2b03")
    }
}