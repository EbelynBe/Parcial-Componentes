package com.example.pit_stops

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pit_stops.modelo.pitStop
import com.example.pit_stops.persistencia.DBHelper
import com.example.pit_stops.persistencia.pitStopLista
import com.example.pit_stops.ui.theme.Pit_StopsTheme

class ListadoPits : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pit_StopsTheme {
                PantallaListadoPitStops()
            }
        }
    }
}

@Composable
fun PantallaListadoPitStops() {
    val context = LocalContext.current
    val dbHelper = remember { DBHelper(context) }
    val gestorPitStops = remember { pitStopLista(dbHelper) }

    var query by remember { mutableStateOf("") }
    var listaPitStops by remember { mutableStateOf(emptyList<pitStop>()) }

    LaunchedEffect(Unit) {
        listaPitStops = gestorPitStops.obtenerTodosLosPitStops()
    }

    val listaFiltrada = remember(query, listaPitStops) {
        if (query.isBlank()) listaPitStops
        else gestorPitStops.buscarPorPiloto(query)
    }

        Scaffold (
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            containerColor = Color.Black
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(padding)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    IconButton(
                        onClick = {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = "Listado de Pit Stops",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    placeholder = { Text("Buscar por piloto...", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(50),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Red,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = Color.White,
                        focusedContainerColor = Color(0xFF101010),
                        unfocusedContainerColor = Color(0xFF101010)
                    )
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(listaFiltrada) { pit ->
                        PitStopCard(pit) {
                            val intent = Intent(context, RegistrarPitStop::class.java)
                            intent.putExtra("isEditMode", true)
                            intent.putExtra("pitStopData", pit)
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PitStopCard(pit: pitStop, onClick: () -> Unit) {
        val colorFondo = Color(0xFF1E1E1E)
        val colorEstado = if (pit.estado) Color(0xFF4CAF50) else Color(0xFFE53935)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorFondo, shape = MaterialTheme.shapes.medium)
                .clickable { onClick() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pit.piloto.nombre,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Escudería: ${pit.escuderia.escuderia}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Tiempo: ${pit.tiempo}s | Neumáticos: ${pit.neumaticosCambiados}",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
                Text(
                    text = "Fecha: ${pit.fechaHora}",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            Text(
                text = if (pit.estado) "OK" else "Fallido",
                color = colorEstado,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.End
            )
        }
    }

