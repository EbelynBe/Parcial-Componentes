package com.example.pit_stops.persistencia

import android.content.ContentValues
import com.example.pit_stops.modelo.tipoCambioNeumatico

class tiposCambioNeumaticoDAO(private val dbHelper: DBHelper) {


    fun insertarTipo(nombre: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("tipo", nombre)
        }
        db.insert("TipoCambioNeumatico", null, values)
        db.close()
    }

    fun obtenerTipos(): List<tipoCambioNeumatico> {
        val lista = mutableListOf<tipoCambioNeumatico>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM TipoCambioNeumatico", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"))
                lista.add(tipoCambioNeumatico(id, tipo))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }
}
