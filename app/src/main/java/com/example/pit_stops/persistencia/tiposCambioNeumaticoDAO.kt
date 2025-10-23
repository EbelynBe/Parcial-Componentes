package com.example.pit_stops.persistencia

import android.content.ContentValues
import com.example.pit_stops.modelo.tipoCambioNeumatico

// Clase DAO (Data Access Object) para manejar las operaciones con la tabla TipoCambioNeumatico
// Permite insertar nuevos tipos y obtener los tipos existentes desde la base de datos
class tiposCambioNeumaticoDAO(private val dbHelper: DBHelper) {

    // Inserta un nuevo tipo de cambio de neumático en la base de datos
    fun insertarTipo(nombre: String) {
        val db = dbHelper.writableDatabase  // Se abre la base de datos en modo escritura
        val values = ContentValues().apply {
            put("tipo", nombre)              // Se agrega el nombre del tipo de neumático
        }
        db.insert("TipoCambioNeumatico", null, values)  // Inserta el nuevo registro
        db.close()                                      // Cierra la base de datos
    }

    // Obtiene todos los tipos de cambio de neumático almacenados en la base de datos
    fun obtenerTipos(): List<tipoCambioNeumatico> {
        val lista = mutableListOf<tipoCambioNeumatico>()    // Lista para guardar los resultados
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM TipoCambioNeumatico", null) // Consulta SQL

        // Recorre los resultados y crea un objeto por cada tipo encontrado
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))     // ID del tipo
                val tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo")) // Nombre del tipo
                lista.add(tipoCambioNeumatico(id, tipo))                       // Se agrega a la lista
            } while (cursor.moveToNext())
        }

        cursor.close()  // Cierra el cursor
        db.close()      // Cierra la base de datos
        return lista    // Devuelve la lista con todos los tipos encontrados
    }
}
