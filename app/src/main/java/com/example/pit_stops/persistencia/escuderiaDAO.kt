package com.example.pit_stops.persistencia

import android.content.ContentValues
import com.example.pit_stops.modelo.escuderia

class escuderiaDAO(private val dbHelper: DBHelper) {

    fun insertarEscuderia(nombre: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("escuderia", nombre)
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
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("escuderia")) // ✅ columna correcta
                lista.add(escuderia(id, nombre))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }
}
