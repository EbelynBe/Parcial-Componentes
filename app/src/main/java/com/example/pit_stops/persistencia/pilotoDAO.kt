package com.example.pit_stops.persistencia

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.pit_stops.modelo.piloto


class pilotoDAO(private val dbHelper: DBHelper) {

    fun insertarPiloto(piloto: piloto): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", piloto.nombre)
        }

        val resultado = db.insert("Piloto", null, values)
        db.close()
        return resultado
    }

    fun obtenerPorId(id: Int): piloto {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Piloto WHERE id = ?", arrayOf(id.toString()))

        var piloto = piloto(0, "")
        if (cursor.moveToFirst()) {
            piloto = piloto(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            )
        }

        cursor.close()
        db.close()
        return piloto
    }

    fun obtenerPilotos(): List<piloto> {
        val lista = mutableListOf<piloto>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Piloto", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                lista.add(piloto(id, nombre))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

}