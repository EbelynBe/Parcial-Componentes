package com.example.pit_stops.persistencia

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.pit_stops.modelo.piloto

// Clase DAO (Data Access Object) para manejar las operaciones con la tabla Piloto
// Permite insertar pilotos y obtenerlos desde la base de datos
class pilotoDAO(private val dbHelper: DBHelper) {

    // Inserta un nuevo piloto en la base de datos
    fun insertarPiloto(piloto: piloto): Long {
        val db: SQLiteDatabase = dbHelper.writableDatabase  // Se abre la base de datos en modo escritura
        val values = ContentValues().apply {
            put("nombre", piloto.nombre)                   // Se agrega el nombre del piloto
        }

        val resultado = db.insert("Piloto", null, values)  // Se inserta el registro en la tabla
        db.close()                                         // Se cierra la base de datos
        return resultado                                   // Se devuelve el ID del nuevo registro
    }

    // Obtiene un piloto específico por su ID
    fun obtenerPorId(id: Int): piloto {
        val db = dbHelper.readableDatabase                 // Se abre la base de datos en modo lectura
        val cursor = db.rawQuery(
            "SELECT * FROM Piloto WHERE id = ?",
            arrayOf(id.toString())
        )

        var piloto = piloto(0, "")                         // Piloto por defecto (en caso de no encontrar coincidencia)
        if (cursor.moveToFirst()) {                        // Si hay resultados...
            piloto = piloto(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            )
        }

        cursor.close()                                     // Se cierra el cursor
        db.close()                                         // Se cierra la base de datos
        return piloto                                      // Se devuelve el piloto encontrado
    }

    // Obtiene la lista completa de pilotos registrados en la base de datos
    fun obtenerPilotos(): List<piloto> {
        val lista = mutableListOf<piloto>()                // Lista donde se guardarán los pilotos
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Piloto", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
                lista.add(piloto(id, nombre))              // Se crea un objeto piloto y se agrega a la lista
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista                                       // Se devuelve la lista de pilotos
    }
}
