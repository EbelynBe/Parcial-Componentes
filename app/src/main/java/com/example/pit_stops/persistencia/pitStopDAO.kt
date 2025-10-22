package com.example.pit_stops.persistencia

import android.content.ContentValues
import com.example.pit_stops.modelo.escuderia
import com.example.pit_stops.modelo.piloto
import com.example.pit_stops.modelo.pitStop
import com.example.pit_stops.modelo.tipoCambioNeumatico

class pitStopDAO (private val dbHelper: DBHelper) {

    fun insertarPitStop(pitStop: pitStop) {
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
        db.insert("PitStop", null, values)
        db.close()
    }

    fun obtenerTodos(): List<pitStop> {
        val lista = mutableListOf<pitStop>()
        val db = dbHelper.readableDatabase

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
        if (cursor.moveToFirst()) {
            do {
                val pitStop = pitStop(
                    id = cursor.getInt(0),
                    tiempo = cursor.getDouble(1),
                    neumaticosCambiados = cursor.getInt(2),
                    estado = cursor.getInt(3) == 1,
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

    fun actualizarPitStop(pitStop: pitStop): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("id_piloto", pitStop.piloto.id)
            put("id_escuderia", pitStop.escuderia.id)
            put("tiempo", pitStop.tiempo)
            put("id_tipo_cambio_neumaticos", pitStop.tipoCambioNeumatico.id)
            put("neumaticosCambiados", pitStop.neumaticosCambiados)
            put("estado", pitStop.estado)
            put("descripcion", pitStop.descripcion)
            put("nombreMecanicoPrincipal", pitStop.nombreMecanicoPrincipal)
            put("fechaHora", pitStop.fechaHora)
        }
        val rows = db.update("PitStop", values, "id=?", arrayOf(pitStop.id.toString()))
        db.close()
        return rows
    }

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
        return tiempos.reversed()
    }

}
