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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pit_stops.ui.theme.Pit_StopsTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

data class PitStop(
    val numero: Int,
    val piloto: String,
    val tiempo: Double,
    val estado: String
)

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
    var query by remember { mutableStateOf("") }

    val listaPits = listOf(
        PitStop(1, "Oliveiro", 2.4, "OK"),
        PitStop(2, "James", 2.8, "Fallido"),
        PitStop(3, "Mark", 2.3, "OK"),
        PitStop(4, "Sebastian", 3.1, "Fallido"),
        PitStop(5, "Lucas", 3.0, "Fallido")
    )

    val listaFiltrada = listaPits.filter {
        it.piloto.contains(query, ignoreCase = true)
    }

    val context = LocalContext.current

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
            // 🔙 Flecha de volver + título
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

            // 🔍 Barra de búsqueda
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Buscar...", color = Color.Gray) },
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

            // 📋 Lista
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listaFiltrada) { pit ->
                    PitStopCard(pit) {
                        val intent = Intent(context, RegistrarPitStop::class.java)
                        context.startActivity(intent)
                    }
                }
            }
        }
    }
}


@Composable
fun PitStopCard(pit: PitStop, onClick: () -> Unit) {
    val colorFondo = Color(0xFF1E1E1E)
    val colorEstado = if (pit.estado == "OK") Color(0xFF4CAF50) else Color(0xFFE53935)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorFondo, shape = MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Número
        Text(
            text = pit.numero.toString(),
            color = Color.Gray,
            fontSize = 20.sp,
            modifier = Modifier.width(30.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Nombre y tiempo
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = pit.piloto,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Tiempo: ${pit.tiempo}s",
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }

        // Estado
        Text(
            text = pit.estado,
            color = colorEstado,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
fun PreviewPitStopList() {
    Pit_StopsTheme {
        PantallaListadoPitStops()
    }
}