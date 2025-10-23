package com.example.pit_stops.persistencia

import android.content.ContentValues
import com.example.pit_stops.modelo.escuderia
import com.example.pit_stops.modelo.piloto
import com.example.pit_stops.modelo.pitStop
import com.example.pit_stops.modelo.tipoCambioNeumatico

// Clase DAO (Data Access Object) para manejar las operaciones CRUD con la tabla PitStop
// Permite insertar, consultar, actualizar y eliminar registros de pit stops en la base de datos
class pitStopDAO(private val dbHelper: DBHelper) {

    // Inserta un nuevo pit stop en la base de datos
    fun insertarPitStop(pitStop: pitStop) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_piloto", pitStop.piloto.id)
            put("id_escuderia", pitStop.escuderia.id)
            put("tiempo", pitStop.tiempo)
            put("id_tipo_cambio_neumaticos", pitStop.tipoCambioNeumatico.id)
            put("neumaticosCambiados", pitStop.neumaticosCambiados)
            put("estado", if (pitStop.estado) 1 else 0)  // Se guarda 1 si es true, 0 si es false
            put("descripcion", pitStop.descripcion)
            put("nombreMecanicoPrincipal", pitStop.nombreMecanicoPrincipal)
            put("fechaHora", pitStop.fechaHora)
        }
        db.insert("PitStop", null, values)  // Inserta el registro en la tabla
        db.close()
    }

    // Obtiene todos los pit stops con su información relacionada (piloto, escudería y tipo de neumático)
    fun obtenerTodos(): List<pitStop> {
        val lista = mutableListOf<pitStop>()
        val db = dbHelper.readableDatabase

        // Consulta SQL con JOIN para unir las tablas relacionadas
        val query = """
            SELECT ps.id, ps.tiempo, ps.neumaticosCambiados, ps.estado, 
                   ps.descripcion, ps.nombreMecanicoPrincipal, ps.fechaHora,
                   p.id, p.nombre, e.id, e.escuderia, t.id, t.tipo
            FROM PitStop ps
            INNER JOIN Piloto p ON ps.id_piloto = p.id
            INNER JOIN Escuderia e ON ps.id_escuderia = e.id
            INNER JOIN TipoCambioNeumatico t ON ps.id_tipo_cambio_neumaticos = t.id
        """

        val cursor = db.rawQuery(query, null)

        // Recorre cada fila del resultado y crea objetos pitStop completos
        if (cursor.moveToFirst()) {
            do {
                val pitStop = pitStop(
                    id = cursor.getInt(0),
                    tiempo = cursor.getDouble(1),
                    neumaticosCambiados = cursor.getInt(2),
                    estado = cursor.getInt(3) == 1, // Se convierte el entero a booleano
                    descripcion = cursor.getString(4),
                    nombreMecanicoPrincipal = cursor.getString(5),
                    fechaHora = cursor.getString(6),
                    piloto = piloto(cursor.getInt(7), cursor.getString(8)),
                    escuderia = escuderia(cursor.getInt(9), cursor.getString(10)),
                    tipoCambioNeumatico = tipoCambioNeumatico(cursor.getInt(11), cursor.getString(12))
                )
                lista.add(pitStop)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    // Actualiza los datos de un pit stop existente
    fun actualizarPitStop(pitStop: pitStop): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_piloto", pitStop.piloto.id)
            put("id_escuderia", pitStop.escuderia.id)
            put("tiempo", pitStop.tiempo)
            put("id_tipo_cambio_neumaticos", pitStop.tipoCambioNeumatico.id)
            put("neumaticosCambiados", pitStop.neumaticosCambiados)
            put("estado", if (pitStop.estado) 1 else 0)
            put("descripcion", pitStop.descripcion)
            put("nombreMecanicoPrincipal", pitStop.nombreMecanicoPrincipal)
            put("fechaHora", pitStop.fechaHora)
        }

        // Se actualiza el registro según su ID
        val rows = db.update("PitStop", values, "id=?", arrayOf(pitStop.id.toString()))
        db.close()
        return rows  // Devuelve el número de filas afectadas
    }

    // Elimina un pit stop según su ID
    fun eliminarPitStop(id: Int): Boolean {
        val db = dbHelper.writableDatabase
        val resultado = db.delete("PitStop", "id=?", arrayOf(id.toString()))
        db.close()
        return resultado > 0  // Devuelve true si se eliminó correctamente
    }

    // Obtiene los últimos 5 tiempos de pit stop registrados (ordenados por el más reciente)
    fun obtenerPitStopsU(): List<Double> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT tiempo FROM PitStop ORDER BY id DESC LIMIT 5", null)
        val tiempos = mutableListOf<Double>()

        if (cursor.moveToFirst()) {
            do {
                tiempos.add(cursor.getDouble(0))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return tiempos.reversed()  // Se devuelven en orden cronológico (de más antiguo a más reciente)
    }
}
