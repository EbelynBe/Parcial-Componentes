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

        // 1️⃣ Guardar estado inicial
        val antes = dao.obtenerEscuderias()
        println("Escuderias antes: $antes")

        // 2️⃣ Insertar escudería temporal
        val nombreTemp = "Test Escuderia"
        dao.insertarEscuderia(nombreTemp)

        // 3️⃣ Consultar y verificar inserción
        val despues = dao.obtenerEscuderias()
        println("Escuderias después de insertar: $despues")

        // ✅ Verificación segura usando toString()
        val existe = despues.any { it.toString().contains(nombreTemp) }
        assertTrue(existe)

        // 4️⃣ Eliminar el último registro insertado (rollback manual)
        val ultimo = despues.last()
        val db = dbHelper.writableDatabase
        val filasEliminadas = db.delete("Escuderia", "id=?", arrayOf(ultimo.id.toString()))
        db.close()
        println("Filas eliminadas (rollback manual): $filasEliminadas")

        // 5️⃣ Confirmar estado final igual al inicial
        val final = dao.obtenerEscuderias()
        println("Escuderias finales: $final")
        assertEquals(antes.size, final.size)
    }
}
