package com.example.pit_stops.modelo

import java.io.Serializable

// Clase que representa una escudería
// Implementa Serializable para poder enviarse entre activitys o guardarse fácilmente
data class escuderia(
    val id: Int,            // Identificador único de la escudería
    val escuderia: String   // Nombre de la escudería
): Serializable
