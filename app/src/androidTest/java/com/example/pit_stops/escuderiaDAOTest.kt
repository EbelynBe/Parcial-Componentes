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


        val antes = dao.obtenerEscuderias()
        println("Escuderias antes: $antes")


        val nombreTemp = "Test Escuderia"
        dao.insertarEscuderia(nombreTemp)


        val despues = dao.obtenerEscuderias()
        println("Escuderias después de insertar: $despues")


        val existe = despues.any { it.toString().contains(nombreTemp) }
        assertTrue(existe)


        val ultimo = despues.last()
        val db = dbHelper.writableDatabase
        val filasEliminadas = db.delete("Escuderia", "id=?", arrayOf(ultimo.id.toString()))
        db.close()
        println("Filas eliminadas (rollback manual): $filasEliminadas")


        val final = dao.obtenerEscuderias()
        println("Escuderias finales: $final")
        assertEquals(antes.size, final.size)
    }
}
