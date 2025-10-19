package com.example.pit_stops.modelo

import java.io.Serializable

data class pitStop(
    val id: Int,
    val piloto: piloto,
    val escuderia: escuderia,
    val tipoCambioNeumatico: tipoCambioNeumatico,
    val tiempo: Double,
    val neumaticosCambiados: Int,
    val estado: Boolean,
    val descripcion: String?,
    val nombreMecanicoPrincipal: String,
    val fechaHora: String
): Serializable
