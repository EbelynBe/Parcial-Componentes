package com.example.pit_stops.persistencia

import android.content.ContentValues
import com.example.pit_stops.modelo.escuderia

class escuderiaDAO (private val dbHelper: DBHelper) {

    fun insertarEscuderia(nombre: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
        }
        db.insert("Escuderia", null, values)
        db.close()
    }

    fun obtenerEscuderias(): List<escuderia> {
        val lista = mutableListOf<escuderia>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Escuderia", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                lista.add(escuderia(id, nombre))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }
}