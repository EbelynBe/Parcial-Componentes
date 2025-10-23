package com.example.pit_stops.modelo

import java.io.Serializable

// Clase que representa a un piloto
data class piloto(
    val id: Int,          // Identificador único del piloto
    val nombre: String    // Nombre del piloto
): Serializable
