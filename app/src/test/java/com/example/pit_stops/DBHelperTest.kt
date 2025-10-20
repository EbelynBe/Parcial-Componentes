package com.example.pit_stops.persistencia

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import android.database.sqlite.SQLiteDatabase

class DBHelperTest {

    @Test
    fun `onCreate ejecuta las sentencias SQL correctamente`() {
        // Simulamos una base de datos
        val mockDb = mockk<SQLiteDatabase>(relaxed = true)


        val context = mockk<android.content.Context>(relaxed = true)
        val dbHelper = DBHelper(context)


        dbHelper.onCreate(mockDb)

        // Verificamos que haya intentado crear las tablas principales
        verify { mockDb.execSQL(match { it.contains("CREATE TABLE Piloto") }) }
        verify { mockDb.execSQL(match { it.contains("CREATE TABLE Escuderia") }) }
        verify { mockDb.execSQL(match { it.contains("CREATE TABLE PitStop") }) }
        verify { mockDb.execSQL(match { it.contains("CREATE TABLE TipoCambioNeumatico") }) }
    }
}
