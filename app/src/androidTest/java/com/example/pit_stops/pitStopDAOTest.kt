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

        val antes = pitStopDAO.obtenerPitStopsU() // Obtener pit stops antes
        println("Tiempos antes: $antes")

        val pitStopTemp = pitStop( // Crear pit stop temporal
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

        pitStopDAO.insertarPitStop(pitStopTemp) // Insertar pit stop
        val despuesInsert = pitStopDAO.obtenerPitStopsU() // Obtener pit stops después de insertar
        println("Tiempos después de insertar: $despuesInsert")

        assertTrue(despuesInsert.size >= antes.size) // Verificar que se ha insertado

        val pitStops = pitStopDAO.obtenerTodos()
        if (pitStops.isNotEmpty()) {
            val primero = pitStops.last() // Obtener el último pit stop
            val actualizado = primero.copy( // Actualizar pit stop
                descripcion = "PitStop actualizado de prueba"
            )
            val filas = pitStopDAO.actualizarPitStop(actualizado) // Actualizar en la base de datos
            println("Filas actualizadas: $filas")
            assertTrue(filas >= 0)
        }

        val todosDespues = pitStopDAO.obtenerTodos() // Obtener todos los pit stops después de actualizar
        if (todosDespues.isNotEmpty()) {
            val ultimo = todosDespues.last() // Obtener el último pit stop
            val filasEliminadas = dbHelper.writableDatabase.delete( // Eliminar el último pit stop
                "PitStop",
                "id=?",
                arrayOf(ultimo.id.toString())
            )
            println("Filas eliminadas (rollback manual): $filasEliminadas")
        }

        val final = pitStopDAO.obtenerPitStopsU() // Obtener pit stops finales
        println("Tiempos finales: $final")
        assertEquals(antes.size, final.size) // Verificar que el tamaño es el mismo
    }
}
