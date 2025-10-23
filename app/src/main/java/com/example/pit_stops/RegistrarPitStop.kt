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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info


// ------------------------------------------------------------
// Clase: RegistrarPitStop
// Descripción: Activity principal para registrar o editar
// pit stops. Permite capturar todos los datos necesarios
// para un pit stop incluyendo piloto, escudería, tiempos,
// tipo de neumático y estado de la operación.
// ------------------------------------------------------------
class RegistrarPitStop : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Determina si está en modo edición o creación
        val isEditMode = intent.getBooleanExtra("isEditMode", false)
        // Obtiene datos del pit stop si está en modo edición
        val pitStopData = try {
            intent.getSerializableExtra("pitStopData") as? pitStop
        } catch (e: Exception) {
            null
        }

        setContent {
            Pit_StopsTheme {
                // Llama al composable principal pasando el modo y datos
                PantallaRegistrarPitStop(isEditMode, pitStopData)
            }
        }
    }
}

// ------------------------------------------------------------
// Función: fieldColors
// Descripción: Define la paleta de colores personalizada para
// los campos de texto de la aplicación. Mantiene consistencia
// visual con el tema oscuro y acentos rojos.
// ------------------------------------------------------------
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

// ------------------------------------------------------------
// Función: DateTimePickerField
// Descripción: Componente personalizado que combina selector
// de fecha y hora en un solo campo. Permite seleccionar fecha
// mediante diálogos nativos y formatea la salida.
// ------------------------------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePickerField(
    label: String,
    value: String,
    onDateTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    val formatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm") }

    // Intenta parsear la fecha/hora actual o usa la actual del sistema
    val currentDateTime = try {
        LocalDateTime.parse(value, formatter)
    } catch (e: Exception) {
        LocalDateTime.now()
    }

    // Configura el calendario con la fecha/hora actual para que el diálogo empiece ahí
    calendar.set(
        currentDateTime.year,
        currentDateTime.monthValue - 1, // Calendar usa 0-11 para meses
        currentDateTime.dayOfMonth,
        currentDateTime.hour,
        currentDateTime.minute
    )

    // Función para mostrar el diálogo de selección de HORA
    val showTimePicker = { year: Int, month: Int, day: Int ->
        TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                // Actualiza el calendario con la hora seleccionada
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)

                // Formatea y llama al callback
                val selectedDateTime = LocalDateTime.of(year, month + 1, day, hour, minute)
                onDateTimeSelected(selectedDateTime.format(formatter))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // Formato 24 horas
        ).show()
    }

    // Función para mostrar el diálogo de selección de FECHA
    val showDatePicker = {
        DatePickerDialog(
            context,
            { _, year: Int, month: Int, day: Int ->
                // Llama al selector de hora después de seleccionar la fecha
                showTimePicker(year, month, day)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    OutlinedTextField(
        value = value,
        onValueChange = {}, // No se permite la edición directa
        readOnly = true,
        label = { Text(label, color = Color.LightGray) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { showDatePicker() }, // Abre el diálogo al hacer click
        shape = RoundedCornerShape(10.dp),
        colors = fieldColors(),
        trailingIcon = {
            Icon(
                imageVector = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Icons.Default.DateRange
                } else {
                    Icons.Default.Info
                },
                contentDescription = "Seleccionar Fecha y Hora",
                tint = Color.Red,
                modifier = Modifier.clickable { showDatePicker() }
            )
        }
    )
}

// ------------------------------------------------------------
// Función: PantallaRegistrarPitStop
// Descripción: Composable principal que renderiza el formulario
// completo para registrar/editar pit stops. Gestiona el estado
// de todos los campos, validaciones y operaciones de BD.
// ------------------------------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistrarPitStop(
    isEditMode: Boolean = false,
    pitStopData: pitStop? = null
) {
    val context = LocalContext.current

    // Inicialización de DAOs para acceso a datos
    val dbHelper = remember { DBHelper(context) }
    val pilotoDao = remember { pilotoDAO(dbHelper) }
    val escuderiaDao = remember { escuderiaDAO(dbHelper) }
    val tiposDao = remember { tiposCambioNeumaticoDAO(dbHelper) }
    val pitDao = remember { pitStopDAO(dbHelper) }

    // Estados para todos los campos del formulario
    var pilotoNombre by remember { mutableStateOf(pitStopData?.piloto?.nombre ?: "") }
    var escuderiaNombre by remember { mutableStateOf(pitStopData?.escuderia?.escuderia ?: "") }
    var tiempoTotal by remember { mutableStateOf(pitStopData?.tiempo?.toString() ?: "") }
    var tipoNeumaticoNombre by remember { mutableStateOf(pitStopData?.tipoCambioNeumatico?.tipo ?: "") }
    var numNeumaticos by remember { mutableStateOf(pitStopData?.neumaticosCambiados?.toString() ?: "") }
    var estadoTexto by remember { mutableStateOf(if (pitStopData?.estado == true) "OK" else (pitStopData?.estado?.let { "Fallido" } ?: "OK")) }
    var motivoFallo by remember { mutableStateOf(pitStopData?.descripcion ?: "") }
    var mecanico by remember { mutableStateOf(pitStopData?.nombreMecanicoPrincipal ?: "") }
    var fechaHora by remember { mutableStateOf(pitStopData?.fechaHora ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))) }

    // Estados para controlar dropdowns expandidos
    var expandPiloto by remember { mutableStateOf(false) }
    var expandEscuderia by remember { mutableStateOf(false) }
    var expandTipo by remember { mutableStateOf(false) }
    var expandEstado by remember { mutableStateOf(false) }

    // Listas de datos para los dropdowns
    var listaPilotos by remember { mutableStateOf(listOf<piloto>()) }
    var listaEscuderias by remember { mutableStateOf(listOf<escuderia>()) }
    var listaTipos by remember { mutableStateOf(listOf<tipoCambioNeumatico>()) }

    // Carga inicial de datos desde la base de datos
    LaunchedEffect(Unit) {
        listaPilotos = pilotoDao.obtenerPilotos()
        listaEscuderias = escuderiaDao.obtenerEscuderias()
        listaTipos = tiposDao.obtenerTipos()
    }

    // Prepara listas de nombres para los dropdowns
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
            // Título dinámico según el modo
            Text(
                text = if (isEditMode) "Editar Pit Stop" else "Registrar Pit Stop",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Campos del formulario
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

            // Campo con validación para solo números
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

            // Selector de fecha y hora personalizado
            DateTimePickerField(
                label = "Fecha y Hora",
                value = fechaHora,
                onDateTimeSelected = { selectedDateTime ->
                    fechaHora = selectedDateTime
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botones de acción
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                // Botón Guardar/Editar
                Button(
                    onClick = {
                        // Validaciones básicas
                        if (pilotoNombre.isBlank() || tiempoTotal.isBlank()) {
                            Toast.makeText(context, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        // Conversión de datos
                        val tiempo = tiempoTotal.toDoubleOrNull() ?: 0.0
                        val numNeum = numNeumaticos.toIntOrNull() ?: 0
                        val estadoBool = estadoTexto == "OK"

                        // Gestión de piloto (crear si no existe)
                        var pilotoObj = listaPilotos.find { it.nombre == pilotoNombre }
                        if (pilotoObj == null) {
                            val newId = pilotoDao.insertarPiloto(piloto(0, pilotoNombre)).toInt()
                            listaPilotos = pilotoDao.obtenerPilotos()
                            pilotoObj = listaPilotos.find { it.id == newId } ?: piloto(newId, pilotoNombre)
                        }

                        // Gestión de escudería (crear si no existe)
                        var escuderiaObj = listaEscuderias.find { it.escuderia == escuderiaNombre }
                        if (escuderiaObj == null) {
                            escuderiaDao.insertarEscuderia(escuderiaNombre)
                            listaEscuderias = escuderiaDao.obtenerEscuderias()
                            escuderiaObj = listaEscuderias.find { it.escuderia == escuderiaNombre } ?: escuderia(0, escuderiaNombre)
                        }

                        // Gestión de tipo de neumático (crear si no existe)
                        var tipoObj = listaTipos.find { it.tipo == tipoNeumaticoNombre }
                        if (tipoObj == null) {
                            tiposDao.insertarTipo(tipoNeumaticoNombre)
                            listaTipos = tiposDao.obtenerTipos()
                            tipoObj = listaTipos.find { it.tipo == tipoNeumaticoNombre } ?: tipoCambioNeumatico(0, tipoNeumaticoNombre)
                        }

                        val fechaStr = fechaHora
                        try {
                            if (isEditMode && pitStopData != null) {
                                // Actualizar pit stop existente
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
                                // Crear nuevo pit stop
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
                            // Regresar al listado
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

                // Botón Eliminar (solo visible en modo editar)
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

                // Botón Cancelar
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

// ------------------------------------------------------------
// Función: DropdownField
// Descripción: Componente reutilizable para dropdown menus.
// Encapsula la lógica de expansión/contracción y selección
// manteniendo consistencia visual con el resto de la app.
// ------------------------------------------------------------
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