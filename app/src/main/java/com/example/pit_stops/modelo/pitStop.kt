package com.example.pit_stops.modelo

import java.io.Serializable

// Clase que representa un pit stop
data class pitStop(
    val id: Int,                         // Identificador del pit stop
    val piloto: piloto,                  // Piloto que realiza la parada
    val escuderia: escuderia,            // Escudería a la que pertenece
    val tipoCambioNeumatico: tipoCambioNeumatico,  // Tipo de cambio de neumáticos
    val tiempo: Double,                  // Tiempo total de la parada
    val neumaticosCambiados: Int,        // Número de neumáticos cambiados
    val estado: Boolean,                 // Estado del pit stop
    val descripcion: String?,            // Descripción opcional del pit stop
    val nombreMecanicoPrincipal: String, // Nombre del mecánico principal a cargo
    val fechaHora: String                // Fecha y hora del pit stop
): Serializable
