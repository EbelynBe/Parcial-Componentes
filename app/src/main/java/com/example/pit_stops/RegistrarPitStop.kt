package com.example.pit_stops

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pit_stops.ui.theme.Pit_StopsTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class RegistrarPitStop : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Pit_StopsTheme {
                PantallaRegistrarPitStop()
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O) //No sirve para cels con API menor a 26, por lo que se usa en la hora, android 8.0
@OptIn(ExperimentalMaterial3Api::class) //DropdownMenuBox, compose material 3
@Composable
fun PantallaRegistrarPitStop() {
    val context = LocalContext.current

    // 🔹 Simulación de datos (luego vendrán desde BD)
    val pilotos = listOf("Lewis Hamilton", "Max Verstappen", "Charles Leclerc", "Lando Norris")
    val escuderias = listOf("Mercedes", "Red Bull", "Ferrari", "McLaren")
    val tiposNeumaticos = listOf("Soft", "Medium", "Hard")
    val estados = listOf("OK", "Fallido")

    // 🔹 Variables
    var piloto by remember { mutableStateOf("") }
    var escuderia by remember { mutableStateOf("") }
    var tiempoTotal by remember { mutableStateOf("") }
    var tipoNeumatico by remember { mutableStateOf("") }
    var numNeumaticos by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }
    var motivoFallo by remember { mutableStateOf("") }
    var mecanico by remember { mutableStateOf("") }
    var fechaHora by remember { mutableStateOf(LocalDateTime.now()) }

    // 🔹 Estados desplegables
    var expandPiloto by remember { mutableStateOf(false) }
    var expandEscuderia by remember { mutableStateOf(false) }
    var expandNeumatico by remember { mutableStateOf(false) }
    var expandEstado by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registrar Pit Stop",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 🔽 Piloto
            DropdownField(
                label = "Piloto",
                value = piloto,
                expanded = expandPiloto,
                options = pilotos,
                onExpandedChange = { expandPiloto = it },
                onOptionSelected = {
                    piloto = it
                    expandPiloto = false
                }
            )

            // 🔽 Escudería
            DropdownField(
                label = "Escudería",
                value = escuderia,
                expanded = expandEscuderia,
                options = escuderias,
                onExpandedChange = { expandEscuderia = it },
                onOptionSelected = {
                    escuderia = it
                    expandEscuderia = false
                }
            )

            // ⏱ Tiempo total
            OutlinedTextField(
                value = tiempoTotal,
                onValueChange = { tiempoTotal = it },
                label = { Text("Tiempo Total (s)", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )

            // 🔽 Tipo de Neumático
            DropdownField(
                label = "Cambio de Neumáticos",
                value = tipoNeumatico,
                expanded = expandNeumatico,
                options = tiposNeumaticos,
                onExpandedChange = { expandNeumatico = it },
                onOptionSelected = {
                    tipoNeumatico = it
                    expandNeumatico = false
                }
            )

            // 🔢 Número de Neumáticos
            OutlinedTextField(
                value = numNeumaticos,
                onValueChange = { numNeumaticos = it },
                label = { Text("Número de Neumáticos Cambiados", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )

            // 🔽 Estado
            DropdownField(
                label = "Estado",
                value = estado,
                expanded = expandEstado,
                options = estados,
                onExpandedChange = { expandEstado = it },
                onOptionSelected = {
                    estado = it
                    expandEstado = false
                }
            )

            // 🧾 Motivo del fallo
            OutlinedTextField(
                value = motivoFallo,
                onValueChange = { motivoFallo = it },
                label = { Text("Motivo del fallo", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )

            // 👨‍🔧 Mecánico principal
            OutlinedTextField(
                value = mecanico,
                onValueChange = { mecanico = it },
                label = { Text("Mecánico principal", color = Color.LightGray) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )

            // 🗓 Fecha y hora (selector funcional)
            val calendar = Calendar.getInstance()
            val dateString = fechaHora.format(dateFormatter)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                OutlinedTextField(
                    value = dateString,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha y Hora del Pit Stop", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors()
                )

                // 👉 Captura clic sobre toda el área
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable {
                            // Selecciona primero la fecha
                            val datePicker = DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    // Luego abre selector de hora
                                    val timePicker = TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            fechaHora = LocalDateTime.of(year, month + 1, day, hour, minute)
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    )
                                    timePicker.show()
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            )
                            datePicker.show()
                        }
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            // 🔘 Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        if (piloto.isBlank() || escuderia.isBlank() || tiempoTotal.isBlank()) {
                            Toast.makeText(context, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Pit Stop guardado correctamente", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val intent = Intent(context, ListadoPits::class.java)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("Cancelar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    value: String,
    expanded: Boolean,
    options: List<String>,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = Color.LightGray) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = fieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.background(Color(0xFF1E1E1E))
        ) {
            options.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, color = Color.White) },
                    onClick = { onOptionSelected(opcion) }
                )
            }
        }
    }
}

@Composable
fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedBorderColor = Color.Red,
    unfocusedBorderColor = Color.Gray,
    cursorColor = Color.White
)
