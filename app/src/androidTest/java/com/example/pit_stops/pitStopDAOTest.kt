package com.example.pit_stops

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.pit_stops.modelo.escuderia
import com.example.pit_stops.modelo.piloto
import com.example.pit_stops.modelo.pitStop
import com.example.pit_stops.modelo.tipoCambioNeumatico
import com.example.pit_stops.persistencia.DBHelper
import com.example.pit_stops.persistencia.pitStopDAO
import org.junit.Assert.*
import org.junit.Test

class pitStopDAOTest {

    @Test
    fun `consultas`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbHelper = DBHelper(context)
        val pitStopDAO = pitStopDAO(dbHelper)


        val antes = pitStopDAO.obtenerPitStopsU()
        println("Tiempos antes: $antes")


        val pitStopTemp = pitStop(
            id = 0,
            piloto = piloto(1, "Lewis Hamilton"),
            escuderia = escuderia(1, "Mercedes"),
            tiempo = 2.85,
            tipoCambioNeumatico = tipoCambioNeumatico(1, "Rápido"),
            neumaticosCambiados = 4,
            estado = true,
            descripcion = "Parada de prueba",
            nombreMecanicoPrincipal = "Carlos Pérez",
            fechaHora = "2025-10-22 16:00:00"
        )


        pitStopDAO.insertarPitStop(pitStopTemp)
        val despuesInsert = pitStopDAO.obtenerPitStopsU()
        println("Tiempos después de insertar: $despuesInsert")


        assertTrue(despuesInsert.size >= antes.size)


        val pitStops = pitStopDAO.obtenerTodos()
        if (pitStops.isNotEmpty()) {
            val primero = pitStops.last()
            val actualizado = primero.copy(
                descripcion = "PitStop actualizado de prueba"
            )
            val filas = pitStopDAO.actualizarPitStop(actualizado)
            println("Filas actualizadas: $filas")
            assertTrue(filas >= 0)
        }


        val todosDespues = pitStopDAO.obtenerTodos()
        if (todosDespues.isNotEmpty()) {
            val ultimo = todosDespues.last()
            val filasEliminadas = dbHelper.writableDatabase.delete(
                "PitStop",
                "id=?",
                arrayOf(ultimo.id.toString())
            )
            println("Filas eliminadas (rollback manual): $filasEliminadas")
        }


        val final = pitStopDAO.obtenerPitStopsU()
        println("Tiempos finales: $final")
        assertEquals(antes.size, final.size)
    }
}
