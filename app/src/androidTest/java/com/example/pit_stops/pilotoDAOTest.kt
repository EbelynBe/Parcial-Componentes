package com.example.pit_stops

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.pit_stops.modelo.piloto
import com.example.pit_stops.persistencia.DBHelper
import com.example.pit_stops.persistencia.pilotoDAO
import org.junit.Assert.*
import org.junit.Test

class pilotoDAOTest {

    @Test
    fun `pruebaPilotoDao`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbHelper = DBHelper(context)
        val dao = pilotoDAO(dbHelper)

        val antes = dao.obtenerPilotos() // Obtener pilotos antes
        println("Pilotos antes: $antes")

        val pilotoTemp = piloto(0, "Test Piloto") // Crear piloto temporal
        val idInsertado = dao.insertarPiloto(pilotoTemp) // Insertar piloto
        assertTrue(idInsertado > 0) // Verificar que se insertó correctamente

        val pilotoGuardado = dao.obtenerPorId(idInsertado.toInt()) // Obtener piloto por ID
        assertNotNull(pilotoGuardado) // Verificar que el piloto existe
        assertEquals("Test Piloto", pilotoGuardado.nombre) // Verificar nombre

        println("Piloto temporal insertado y consultado correctamente")

        val db = dbHelper.writableDatabase
        val filasEliminadas = db.delete("Piloto", "id=?", arrayOf(idInsertado.toString())) // Eliminar piloto
        db.close()
        println("Filas eliminadas (rollback manual): $filasEliminadas")

        val final = dao.obtenerPilotos() // Obtener pilotos después de eliminar
        println("Pilotos finales: $final")
        assertEquals(antes.size, final.size) // Verificar que el tamaño es el mismo
    }
}
