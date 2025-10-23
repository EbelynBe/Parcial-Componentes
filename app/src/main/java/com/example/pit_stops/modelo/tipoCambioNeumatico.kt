package com.example.pit_stops.modelo

import java.io.Serializable

// Clase que representa el tipo de cambio de neumáticos
data class tipoCambioNeumatico(
    val id: Int,         // Identificador del tipo
    val tipo: String     // Descripción del tipo de cambio
): Serializable
