package com.example.pit_stops.persistencia

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "pitstops.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
        CREATE TABLE Piloto (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL
        );
    """)

        db?.execSQL("""
        CREATE TABLE Escuderia (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            escuderia TEXT NOT NULL
        );
    """)

        db?.execSQL("""
        CREATE TABLE TipoCambioNeumatico (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            tipo TEXT NOT NULL
        );
    """)

        db?.execSQL("""
        CREATE TABLE PitStop (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            id_piloto INTEGER NOT NULL,
            id_escuderia INTEGER NOT NULL,
            tiempo REAL NOT NULL,
            id_tipo_cambio_neumaticos INTEGER NOT NULL,
            neumaticosCambiados INTEGER,
            estado INTEGER CHECK (estado IN (0, 1)) NOT NULL,
            descripcion TEXT,
            nombreMecanicoPrincipal TEXT,
            fechaHora TEXT NOT NULL,
            FOREIGN KEY (id_piloto) REFERENCES Piloto(id),
            FOREIGN KEY (id_escuderia) REFERENCES Escuderia(id),
            FOREIGN KEY (id_tipo_cambio_neumaticos) REFERENCES TipoCambioNeumatico(id)
        );
    """)
        db?.execSQL("INSERT INTO Piloto(nombre) VALUES ('Lewis Hamilton')")
        db?.execSQL("INSERT INTO Piloto(nombre) VALUES ('Max Verstappen')")
        db?.execSQL("INSERT INTO Escuderia(escuderia) VALUES ('Mercedes')")
        db?.execSQL("INSERT INTO Escuderia(escuderia) VALUES ('Red Bull')")
        db?.execSQL("INSERT INTO TipoCambioNeumatico(tipo) VALUES ('Rápido')")
        db?.execSQL("INSERT INTO TipoCambioNeumatico(tipo) VALUES ('Completo')")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS PitStop")
        db?.execSQL("DROP TABLE IF EXISTS Piloto")
        db?.execSQL("DROP TABLE IF EXISTS Escuderia")
        db?.execSQL("DROP TABLE IF EXISTS TipoCambioNeumatico")
        onCreate(db)
    }


}