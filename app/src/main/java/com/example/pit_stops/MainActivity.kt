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
import com.example.pit_stops.persistencia.DBHelper
import com.example.pit_stops.persistencia.pitStopDAO

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dbHelper = DBHelper(this)
        val pitStopDAO = pitStopDAO(dbHelper)
        val tiempos = pitStopDAO.obtenerPitStopsU()


        setContent {
            Inicio(
                tiempos = tiempos,
                onRegistrarPitStop = {
                    val intent = Intent(this, RegistrarPitStop::class.java)
                    intent.putExtra("isEditMode", false)
                    startActivity(intent)
                },
                onVerListado = {
                    val intent = Intent(this, ListadoPits::class.java)
                    startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun Inicio(
    tiempos: List<Double>,
    onRegistrarPitStop: () -> Unit,
    onVerListado: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Encabezado con título y gráfica
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF000000),
                            Color(0xFF600808)
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

            // Gráfica justo debajo del texto
            // Gráfica justo debajo del texto
            if (tiempos.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(250.dp) // un poco más de altura para título y etiquetas
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
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
                            30f, // margen desde arriba
                            textPaint
                        )

                        // Eje Y
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x = 60f, y = 50f),
                            end = Offset(x = 60f, y = ejeBase),
                            strokeWidth = 3f
                        )
                        // Eje X
                        drawLine(
                            color = Color.Gray,
                            start = Offset(x = 60f, y = ejeBase),
                            end = Offset(x = size.width, y = ejeBase),
                            strokeWidth = 3f
                        )

                        // Etiqueta eje Y
                        drawContext.canvas.nativeCanvas.drawText(
                            "Tiempo (s)",
                            30f,
                            ejeBase / 2,
                            textPaint.apply { textAlign = android.graphics.Paint.Align.CENTER; textSize = 22f }
                        )

                        // Barras
                        tiempos.forEachIndexed { index, valor ->
                            val barHeight = (valor / maxTiempo * (ejeBase - 70)).toFloat() // descontar margen superior
                            val x = 90f + index * (barWidth * 2)
                            val y = ejeBase - barHeight

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

                            // Número de pit stop abajo de la barra
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

        // Contenido inferior (tarjeta + botones)
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 380.dp),
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

                        Text(
                            text = if (tiempos.isNotEmpty()) "${"%.2f".format(tiempos.minOrNull())} s" else "No hay registros",
                            color = Color(0xFFFF5555),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Promedio de tiempos: ${
                                if (tiempos.isNotEmpty()) "%.2f".format(tiempos.average()) else "0.0"
                            } s",
                            color = Color.LightGray,
                            fontSize = 18.sp
                        )

                        Text(
                            text = "Total de paradas: ${tiempos.size}",
                            color = Color.LightGray,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                    Text("Registrar Pit Stop", color = Color.White, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

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
                    Text("Ver Listado", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}
