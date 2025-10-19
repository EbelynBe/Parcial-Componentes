package com.example.pit_stops.persistencia

import android.content.ContentValues
import com.example.pit_stops.modelo.tipoCambioNeumatico

class tiposCambioNeumaticoDAO (private val dbHelper: DBHelper) {

    fun insertarTipo(nombre: String) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
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
                val id = cursor.getInt(0)
                val nombre = cursor.getString(1)
                lista.add(tipoCambioNeumatico(id, nombre))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }
}