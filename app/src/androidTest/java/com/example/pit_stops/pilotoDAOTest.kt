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


        val antes = dao.obtenerPilotos()
        println("Pilotos antes: $antes")


        val pilotoTemp = piloto(0, "Test Piloto")
        val idInsertado = dao.insertarPiloto(pilotoTemp)
        assertTrue(idInsertado > 0)


        val pilotoGuardado = dao.obtenerPorId(idInsertado.toInt())
        assertNotNull(pilotoGuardado)
        assertEquals("Test Piloto", pilotoGuardado.nombre)



        println("Piloto temporal insertado y consultado correctamente")


        val db = dbHelper.writableDatabase
        val filasEliminadas = db.delete("Piloto", "id=?", arrayOf(idInsertado.toString()))
        db.close()
        println("Filas eliminadas (rollback manual): $filasEliminadas")


        val final = dao.obtenerPilotos()
        println("Pilotos finales: $final")
        assertEquals(antes.size, final.size)
    }
}
