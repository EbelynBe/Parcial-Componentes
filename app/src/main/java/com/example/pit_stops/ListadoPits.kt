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
import com.example.pit_stops.persistencia.pitStopDAO
import com.example.pit_stops.ui.theme.Pit_StopsTheme

// ------------------------------------------------------------
// Clase: ListadoPits
// Descripción: Pantalla que muestra el listado de todos los Pit Stops
// registrados en la base de datos. Permite buscar por nombre de piloto
// y acceder a la edición de un registro al seleccionarlo.
// ------------------------------------------------------------
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
    val dbHelper = remember { DBHelper(context) }        // Helper para manejar la base de datos
    val pitStopDAO = remember { pitStopDAO(dbHelper) }   // DAO para acceder a los pit stops

    var query by remember { mutableStateOf("") }          // Texto de búsqueda
    var listaPitStops by remember { mutableStateOf(emptyList<pitStop>()) } // Lista total de pit stops

    // Carga inicial de los registros desde la base de datos
    LaunchedEffect(key1 = true) {
        listaPitStops = pitStopDAO.obtenerTodos()
    }

    // Filtrado local según el nombre del piloto
    val listaFiltrada = remember(query, listaPitStops) {
        if (query.isBlank()) listaPitStops
        else listaPitStops.filter {
            it.piloto.nombre.contains(query, ignoreCase = true)
        }
    }

    // Estructura visual de la pantalla usando Scaffold
    Scaffold(
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
            // Encabezado con botón de volver y título
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

            // Campo de búsqueda por nombre del piloto
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

            // Lista con los pit stops (usa LazyColumn para scroll eficiente)
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listaFiltrada) { pit ->
                    PitStopCard(pit) {
                        // Al hacer clic en un item, se abre la pantalla de edición
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

// ------------------------------------------------------------
// Composable: PitStopCard
// Descripción: Muestra los datos principales de un pit stop
// en una tarjeta con diseño oscuro y estado visual (OK / Fallido).
// ------------------------------------------------------------
@Composable
fun PitStopCard(pit: pitStop, onClick: () -> Unit) {
    val colorFondo = Color(0xFF1E1E1E)
    val colorEstado = if (pit.estado) Color(0xFF4CAF50) else Color(0xFFE53935)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorFondo, shape = MaterialTheme.shapes.medium)
            .clickable { onClick() }        // Permite navegar al hacer clic
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

        // Indicador visual del estado del pit stop
        Text(
            text = if (pit.estado) "OK" else "Fallido",
            color = colorEstado,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.End
        )
    }
}
