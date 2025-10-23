package com.example.pit_stops

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.pit_stops.persistencia.DBHelper
import com.example.pit_stops.persistencia.escuderiaDAO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class escuderiaDAOTest {

    @Test
    fun `pruebaescuderiaDAo`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbHelper = DBHelper(context)
        val dao = escuderiaDAO(dbHelper)

        val antes = dao.obtenerEscuderias() // Obtener escuderías antes
        println("Escuderias antes: $antes")

        val nombreTemp = "Test Escuderia" // Nombre temporal para escudería
        dao.insertarEscuderia(nombreTemp) // Insertar escudería

        val despues = dao.obtenerEscuderias() // Obtener escuderías después de insertar
        println("Escuderias después de insertar: $despues")

        val existe = despues.any { it.toString().contains(nombreTemp) } // Verificar existencia de la escudería
        assertTrue(existe)

        val ultimo = despues.last() // Obtener última escudería
        val db = dbHelper.writableDatabase
        val filasEliminadas = db.delete("Escuderia", "id=?", arrayOf(ultimo.id.toString())) // Eliminar escudería
        db.close()
        println("Filas eliminadas (rollback manual): $filasEliminadas")

        val final = dao.obtenerEscuderias() // Obtener escuderías después de eliminar
        println("Escuderias finales: $final")
        assertEquals(antes.size, final.size) // Verificar que el tamaño es el mismo
    }
}
