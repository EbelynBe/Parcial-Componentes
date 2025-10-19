package com.example.pit_stops.persistencia

import com.example.pit_stops.modelo.pitStop

class pitStopLista (private val dbHelper: DBHelper) {

    private val pitStopDAO = pitStopDAO(dbHelper)
    private val pilotoDAO = pilotoDAO(dbHelper)
    private val escuderiaDAO = escuderiaDAO(dbHelper)
    private val tipoCambioDAO = tiposCambioNeumaticoDAO(dbHelper)

    fun obtenerTodosLosPitStops(): List<pitStop> {
        return pitStopDAO.obtenerTodos()
    }

    fun buscarPorPiloto(nombre: String): List<pitStop> {
        val todos = pitStopDAO.obtenerTodos()
        return todos.filter { it.piloto.nombre.contains(nombre, ignoreCase = true) }
    }

    fun filtrarPorEscuderia(nombreEscuderia: String): List<pitStop> {
        val todos = pitStopDAO.obtenerTodos()
        return todos.filter { it.escuderia.escuderia.contains(nombreEscuderia, ignoreCase = true) }
    }

    fun filtrarPorEstado(ok: Boolean): List<pitStop> {
        val todos = pitStopDAO.obtenerTodos()
        return todos.filter { it.estado == ok }
    }

    fun filtrarPorFecha(fecha: String): List<pitStop> {
        val todos = pitStopDAO.obtenerTodos()
        return todos.filter { it.fechaHora.startsWith(fecha) }
    }
}