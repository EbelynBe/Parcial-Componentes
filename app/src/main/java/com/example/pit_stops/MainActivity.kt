package com.example.pit_stops

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pit_stops.modelo.pitStop
import com.example.pit_stops.persistencia.DBHelper
import com.example.pit_stops.persistencia.pitStopDAO
import com.example.pit_stops.ui.theme.Pit_StopsTheme
import com.example.pit_stops.ui.theme.vipnagorgiallaFamily

// ------------------------------------------------------------
// Clase: MainActivity
// Descripción: Pantalla principal de la aplicación. Muestra
// un resumen gráfico con los últimos tiempos de pit stop,
// estadísticas básicas y botones para registrar o listar.
// ------------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = DBHelper(this)
        val pitStopDAO = pitStopDAO(dbHelper)
        val tiempos = pitStopDAO.obtenerPitStopsU() // Obtiene los últimos tiempos registrados
        val tiemposTotal = pitStopDAO.obtenerTodos()
        setContent {
            Pit_StopsTheme {
                // Llama al composable principal pasando los datos y acciones
                Inicio(
                    tiempos = tiempos, tiemposTotal = tiemposTotal,
                    onRegistrarPitStop = {
                        // Abre la pantalla para registrar un nuevo pit stop
                        val intent = Intent(this, RegistrarPitStop::class.java)
                        intent.putExtra("isEditMode", false)
                        startActivity(intent)
                    },
                    onVerListado = {
                        // Abre la pantalla con el listado de pit stops
                        val intent = Intent(this, ListadoPits::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

// ------------------------------------------------------------
// Composable: Inicio
// Descripción: Interfaz principal con la gráfica de barras
// que muestra los tiempos recientes, junto a estadísticas
// generales y botones de navegación.
// ------------------------------------------------------------
@Composable
fun Inicio(
    tiempos: List<Double>,
    tiemposTotal: List<pitStop>,
    onRegistrarPitStop: () -> Unit,
    onVerListado: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // ---------- Sección superior: Gráfica y título ----------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF000000), // negro en la parte superior
                            Color(0xFF600808)  // rojo oscuro degradado
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Resumen de Pit Stops",
                color = Color.Gray,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Si existen datos, se muestra la gráfica; si no, un texto informativo
            if (tiempos.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(250.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    // Canvas para dibujar la gráfica de barras manualmente
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        val maxTiempo = tiempos.maxOrNull() ?: 1.0
                        val barWidth = size.width / (tiempos.size * 2)
                        val ejeBase = size.height * 0.75f
                        val textPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.BLACK
                            textSize = 28f
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }

                        // Título de la gráfica
                        drawContext.canvas.nativeCanvas.drawText(
                            "Últimos Pit Stops",
                            size.width / 2,
                            30f,
                            textPaint
                        )

                        // Dibuja los ejes X e Y
                        drawLine(
                            color = Color.Gray,
                            start = Offset(60f, 50f),
                            end = Offset(60f, ejeBase),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = Color.Gray,
                            start = Offset(60f, ejeBase),
                            end = Offset(size.width, ejeBase),
                            strokeWidth = 3f
                        )

                        // Etiqueta del eje Y
                        drawContext.canvas.nativeCanvas.drawText(
                            "Tiempo (s)",
                            30f,
                            ejeBase / 2,
                            textPaint.apply {
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = 22f
                            }
                        )

                        // Dibuja las barras con sus valores y etiquetas
                        tiempos.forEachIndexed { index, valor ->
                            val barHeight = (valor / maxTiempo * (ejeBase - 70)).toFloat()
                            val x = 90f + index * (barWidth * 2)
                            val y = ejeBase - barHeight

                            // Barra roja
                            drawRect(
                                color = Color(0xFFD32F2F),
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight)
                            )

                            // Valor arriba de la barra
                            drawContext.canvas.nativeCanvas.drawText(
                                String.format("%.1f", valor),
                                x + barWidth / 2,
                                y - 10f,
                                textPaint
                            )

                            // Etiqueta inferior (P1, P2, P3...)
                            drawContext.canvas.nativeCanvas.drawText(
                                "P${index + 1}",
                                x + barWidth / 2,
                                ejeBase + 35f,
                                textPaint
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "No hay datos de pit stops registrados",
                    color = Color.LightGray,
                    fontSize = 16.sp
                )
            }
        }

        // ---------- Sección inferior: estadísticas y botones ----------
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 380.dp), // se superpone sobre la parte superior
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
            color = Color(0xFF050505),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp, bottom = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // Tarjeta con estadísticas rápidas
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(170.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A1A)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Pit stop más rápido:",
                            color = Color.LightGray,
                            fontSize = 20.sp
                        )

                        // Muestra el menor tiempo (mejor pit stop)
                        Text(
                            text = if (tiempos.isNotEmpty())
                                "${"%.2f".format(tiempos.minOrNull())} s"
                            else "No hay registros",
                            color = Color(0xFFFF5555),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Promedio de tiempos
                        Text(
                            text = "Promedio de tiempos:",
                            color = Color.LightGray,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "${if (tiemposTotal.isNotEmpty()) "%.2f".format(tiemposTotal.map { it.tiempo }.average()) else "0.0"
                        } s",
                            color = Color.LightGray,
                            fontSize = 18.sp
                        )

                        // Total de registros en la gráfica
                        Text(
                            text = "Total de paradas: ${tiemposTotal.size}",
                            color = Color.LightGray,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón para registrar nuevo pit stop
                ElevatedButton(
                    onClick = onRegistrarPitStop,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xFFD32F2F)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Registrar Pit Stop",
                        color = Color.White,
                        fontFamily = vipnagorgiallaFamily,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Botón para ver listado completo
                ElevatedButton(
                    onClick = onVerListado,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = Color(0xFF333333)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Ver Listado",
                        color = Color.White,
                        fontFamily = vipnagorgiallaFamily,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
