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
import com.example.pit_stops.modelo.escuderia
import com.example.pit_stops.modelo.piloto
import com.example.pit_stops.modelo.pitStop
import com.example.pit_stops.modelo.tipoCambioNeumatico
import com.example.pit_stops.persistencia.DBHelper
import com.example.pit_stops.persistencia.escuderiaDAO
import com.example.pit_stops.persistencia.pilotoDAO
import com.example.pit_stops.persistencia.pitStopDAO
import com.example.pit_stops.persistencia.tiposCambioNeumaticoDAO
import com.example.pit_stops.ui.theme.Pit_StopsTheme
import com.example.pit_stops.ui.theme.vipnagorgiallaFamily
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class RegistrarPitStop : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isEditMode = intent.getBooleanExtra("isEditMode", false)
        val pitStopData = try {
            intent.getSerializableExtra("pitStopData") as? pitStop
        } catch (e: Exception) {
            null
        }


        setContent {
            Pit_StopsTheme {
                PantallaRegistrarPitStop(isEditMode, pitStopData)
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
    cursorColor = Color.White,
    focusedContainerColor = Color(0xFF101010),
    unfocusedContainerColor = Color(0xFF101010)
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistrarPitStop(
    isEditMode: Boolean = false,
    pitStopData: pitStop? = null
) {
    val context = LocalContext.current

    val dbHelper = remember { DBHelper(context) }
    val pilotoDao = remember { pilotoDAO(dbHelper) }
    val escuderiaDao = remember { escuderiaDAO(dbHelper) }
    val tiposDao = remember { tiposCambioNeumaticoDAO(dbHelper) }
    val pitDao = remember { pitStopDAO(dbHelper) }

    var pilotoNombre by remember { mutableStateOf(pitStopData?.piloto?.nombre ?: "") }
    var escuderiaNombre by remember { mutableStateOf(pitStopData?.escuderia?.escuderia ?: "") }
    var tiempoTotal by remember { mutableStateOf(pitStopData?.tiempo?.toString() ?: "") }
    var tipoNeumaticoNombre by remember { mutableStateOf(pitStopData?.tipoCambioNeumatico?.tipo ?: "") }
    var numNeumaticos by remember { mutableStateOf(pitStopData?.neumaticosCambiados?.toString() ?: "") }
    var estadoTexto by remember { mutableStateOf(if (pitStopData?.estado == true) "OK" else (pitStopData?.estado?.let { "Fallido" } ?: "OK")) }
    var motivoFallo by remember { mutableStateOf(pitStopData?.descripcion ?: "") }
    var mecanico by remember { mutableStateOf(pitStopData?.nombreMecanicoPrincipal ?: "") }

    var fechaHora by remember { mutableStateOf(pitStopData?.fechaHora ?: java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))) }

    var expandPiloto by remember { mutableStateOf(false) }
    var expandEscuderia by remember { mutableStateOf(false) }
    var expandTipo by remember { mutableStateOf(false) }
    var expandEstado by remember { mutableStateOf(false) }

    var listaPilotos by remember { mutableStateOf(listOf<piloto>()) }
    var listaEscuderias by remember { mutableStateOf(listOf<escuderia>()) }
    var listaTipos by remember { mutableStateOf(listOf<tipoCambioNeumatico>()) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    val calendar = remember { Calendar.getInstance() }

    LaunchedEffect(Unit) {
        listaPilotos = pilotoDao.obtenerPilotos()
        listaEscuderias = escuderiaDao.obtenerEscuderias()
        listaTipos = tiposDao.obtenerTipos()
    }

    val pilotosNombres = listaPilotos.map { it.nombre }
    val escuderiasNombres = listaEscuderias.map { it.escuderia }
    val tiposNombres = listaTipos.map { it.tipo }
    val estados = listOf("OK", "Fallido")

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
                text = if (isEditMode) "Editar Pit Stop" else "Registrar Pit Stop",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DropdownField(
                label = "Piloto",
                value = pilotoNombre,
                expanded = expandPiloto,
                options = pilotosNombres,
                onExpandedChange = { expandPiloto = it }
            ) { seleccionado ->
                pilotoNombre = seleccionado
                expandPiloto = false
            }

            DropdownField(
                label = "Escudería",
                value = escuderiaNombre,
                expanded = expandEscuderia,
                options = escuderiasNombres,
                onExpandedChange = { expandEscuderia = it }
            ) { seleccionado ->
                escuderiaNombre = seleccionado
                expandEscuderia = false
            }

            OutlinedTextField(
                value = tiempoTotal,
                onValueChange = { value -> tiempoTotal = value },
                label = { Text("Tiempo Total (s)", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )

            DropdownField(
                label = "Tipo de Neumático",
                value = tipoNeumaticoNombre,
                expanded = expandTipo,
                options = tiposNombres,
                onExpandedChange = { expandTipo = it }
            ) { seleccionado ->
                tipoNeumaticoNombre = seleccionado
                expandTipo = false
            }

            OutlinedTextField(
                value = numNeumaticos,
                onValueChange = { value -> numNeumaticos = value.filter { ch -> ch.isDigit() } },
                label = { Text("Número de Neumáticos Cambiados", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )

            val estados = listOf("OK", "Fallido")
            DropdownField(
                label = "Estado",
                value = estadoTexto,
                expanded = expandEstado,
                options = estados,
                onExpandedChange = { expandEstado = it }
            ) { seleccionado ->
                estadoTexto = seleccionado
                expandEstado = false
            }

            OutlinedTextField(
                value = motivoFallo,
                onValueChange = { value -> motivoFallo = value },
                label = { Text("Motivo del fallo", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )

            OutlinedTextField(
                value = mecanico,
                onValueChange = { value -> mecanico = value },
                label = { Text("Mecánico principal", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )

            OutlinedTextField(
                value = fechaHora,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha y Hora", color = Color.LightGray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    onClick = {
                        if (pilotoNombre.isBlank() || tiempoTotal.isBlank()) {
                            Toast.makeText(context, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val tiempo = tiempoTotal.toDoubleOrNull() ?: 0.0
                        val numNeum = numNeumaticos.toIntOrNull() ?: 0
                        val estadoBool = estadoTexto == "OK"

                        var pilotoObj = listaPilotos.find { it.nombre == pilotoNombre }
                        if (pilotoObj == null) {
                            val newId = pilotoDao.insertarPiloto(piloto(0, pilotoNombre)).toInt()
                            listaPilotos = pilotoDao.obtenerPilotos()
                            pilotoObj = listaPilotos.find { it.id == newId } ?: piloto(newId, pilotoNombre)
                        }
                        var escuderiaObj = listaEscuderias.find { it.escuderia == escuderiaNombre }
                        if (escuderiaObj == null) {
                            escuderiaDao.insertarEscuderia(escuderiaNombre)
                            listaEscuderias = escuderiaDao.obtenerEscuderias()
                            escuderiaObj = listaEscuderias.find { it.escuderia == escuderiaNombre } ?: escuderia(0, escuderiaNombre)
                        }
                        var tipoObj = listaTipos.find { it.tipo == tipoNeumaticoNombre }
                        if (tipoObj == null) {
                            tiposDao.insertarTipo(tipoNeumaticoNombre)
                            listaTipos = tiposDao.obtenerTipos()
                            tipoObj = listaTipos.find { it.tipo == tipoNeumaticoNombre } ?: tipoCambioNeumatico(0, tipoNeumaticoNombre)
                        }

                        val fechaStr = fechaHora
                        try {
                            if (isEditMode && pitStopData != null) {
                                val actualizado = pitStop(
                                    id = pitStopData.id,
                                    piloto = pilotoObj,
                                    escuderia = escuderiaObj,
                                    tipoCambioNeumatico = tipoObj,
                                    tiempo = tiempo,
                                    neumaticosCambiados = numNeum,
                                    estado = estadoBool,
                                    descripcion = motivoFallo.ifBlank { null },
                                    nombreMecanicoPrincipal = mecanico,
                                    fechaHora = fechaStr
                                )
                                pitDao.actualizarPitStop(actualizado)
                                Toast.makeText(context, "Pit Stop actualizado", Toast.LENGTH_SHORT).show()
                            } else {
                                val nuevo = pitStop(
                                    id = 0,
                                    piloto = pilotoObj,
                                    escuderia = escuderiaObj,
                                    tipoCambioNeumatico = tipoObj,
                                    tiempo = tiempo,
                                    neumaticosCambiados = numNeum,
                                    estado = estadoBool,
                                    descripcion = motivoFallo.ifBlank { null },
                                    nombreMecanicoPrincipal = mecanico,
                                    fechaHora = fechaStr
                                )
                                pitDao.insertarPitStop(nuevo)
                                Toast.makeText(context, "Pit Stop guardado", Toast.LENGTH_SHORT).show()
                            }
                            context.startActivity(Intent(context, ListadoPits::class.java))
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error guardando: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(end = if (isEditMode) 4.dp else 8.dp)
                ) {
                    Text(if (isEditMode) "Editar" else "Guardar", color = Color.White, fontSize = 12.sp,fontFamily = vipnagorgiallaFamily, fontWeight = FontWeight.Light)
                }

                if (isEditMode && pitStopData != null) {
                    Button(
                        onClick = {
                            val eliminado = pitDao.eliminarPitStop(pitStopData.id)
                            if (eliminado) {
                                Toast.makeText(context, "Pit Stop eliminado", Toast.LENGTH_SHORT).show()
                                context.startActivity(Intent(context, ListadoPits::class.java))
                            } else {
                                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                    ) {
                        Text("Eliminar", color = Color.White, fontSize = 12.sp,fontFamily = vipnagorgiallaFamily, fontWeight = FontWeight.Light)
                    }
                }

                Button(
                    onClick = {
                        context.startActivity(Intent(context, ListadoPits::class.java))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(start = if (isEditMode) 4.dp else 8.dp)
                ) {
                    Text("Cancelar", color = Color.White, fontSize = 12.sp,fontFamily = vipnagorgiallaFamily, fontWeight = FontWeight.Light)
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
        onExpandedChange = { onExpandedChange(!expanded) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        OutlinedTextField(
            readOnly = true,
            value = value,
            onValueChange = {},
            label = { Text(label, color = Color.LightGray) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = fieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                    }
                )
            }
        }
    }
}


