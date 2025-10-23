package com.example.pit_stops

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.pit_stops.persistencia.DBHelper
import com.example.pit_stops.persistencia.tiposCambioNeumaticoDAO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class tiposCambioNeumaticoDAOTest {

    @Test
    fun `pruebatiposCambioNeumaticoDAO`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbHelper = DBHelper(context)
        val dao = tiposCambioNeumaticoDAO(dbHelper)

        val antes = dao.obtenerTipos() // Obtener tipos antes
        val nombreTemp = "Test Tipo"
        dao.insertarTipo(nombreTemp) // Insertar tipo

        val despues = dao.obtenerTipos() // Obtener tipos después
        val existe = despues.any { it.toString().contains(nombreTemp) }
        assertTrue(existe)

        val ultimo = despues.last()
        val db = dbHelper.writableDatabase
        val filasEliminadas = db.delete("TipoCambioNeumatico", "id=?", arrayOf(ultimo.id.toString())) // Eliminar último tipo
        db.close()

        val final = dao.obtenerTipos() // Obtener tipos finales
        assertEquals(antes.size, final.size) // Comparar tamaños
    }
}

