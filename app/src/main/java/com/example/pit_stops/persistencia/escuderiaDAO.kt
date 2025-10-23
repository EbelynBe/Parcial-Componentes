package com.example.pit_stops.persistencia

import android.content.ContentValues
import com.example.pit_stops.modelo.escuderia

// Clase DAO (Data Access Object) para manejar las operaciones con la tabla Escuderia
// Se encarga de insertar y obtener datos de la base de datos
class escuderiaDAO(private val dbHelper: DBHelper) {

    // Inserta una nueva escudería en la base de datos
    fun insertarEscuderia(nombre: String) {
        val db = dbHelper.writableDatabase  // Se obtiene la base de datos en modo escritura
        val values = ContentValues().apply {
            put("escuderia", nombre)        // Se asigna el valor al campo "escuderia"
        }
        db.insert("Escuderia", null, values) // Se inserta el registro
        db.close()                           // Se cierra la conexión con la base de datos
    }

    // Obtiene todas las escuderías guardadas en la base de datos
    fun obtenerEscuderias(): List<escuderia> {
        val lista = mutableListOf<escuderia>()           // Lista donde se guardarán los resultados
        val db = dbHelper.readableDatabase               // Se obtiene la base de datos en modo lectura
        val cursor = db.rawQuery("SELECT * FROM Escuderia", null) // Se ejecuta la consulta SQL

        // Recorre los resultados y crea objetos "escuderia" por cada fila encontrada
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))              // ID de la escudería
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("escuderia")) // Nombre de la escudería
                lista.add(escuderia(id, nombre))                                         // Se agrega a la lista
            } while (cursor.moveToNext())
        }

        // Se cierran los recursos
        cursor.close()
        db.close()

        return lista  // Se devuelve la lista con todas las escuderías
    }
}
