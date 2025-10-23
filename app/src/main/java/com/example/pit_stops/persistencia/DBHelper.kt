package com.example.pit_stops.persistencia

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Clase que maneja la base de datos local de la app (SQLite)
// Se encarga de crear, actualizar y poblar las tablas con datos iniciales
class DBHelper(context: Context) : SQLiteOpenHelper(context, "pitstops.db", null, 2) {

    // Se ejecuta la primera vez que se crea la base de datos
    override fun onCreate(db: SQLiteDatabase?) {
        // Creación de la tabla de pilotos
        db?.execSQL("""
        CREATE TABLE Piloto (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            nombre TEXT NOT NULL
        );
    """)

        // Creación de la tabla de escuderías
        db?.execSQL("""
        CREATE TABLE Escuderia (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            escuderia TEXT NOT NULL
        );
    """)

        // Creación de la tabla de tipos de cambio de neumáticos
        db?.execSQL("""
        CREATE TABLE TipoCambioNeumatico (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            tipo TEXT NOT NULL
        );
    """)

        // Creación de la tabla de pit stops con sus relaciones
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

        // Inserción de datos iniciales para probar la app

        // Pilotos
        db?.execSQL("INSERT INTO Piloto(nombre) VALUES ('Lewis Hamilton')")
        db?.execSQL("INSERT INTO Piloto(nombre) VALUES ('Max Verstappen')")
        db?.execSQL("INSERT INTO Piloto(nombre) VALUES ('Charles Leclerc')")
        db?.execSQL("INSERT INTO Piloto(nombre) VALUES ('Lando Norris')")
        db?.execSQL("INSERT INTO Piloto(nombre) VALUES ('Oliver Bearman')")
        db?.execSQL("INSERT INTO Piloto(nombre) VALUES ('Fernando Alonso')")

        // Escuderías
        db?.execSQL("INSERT INTO Escuderia(escuderia) VALUES ('Mercedes')")
        db?.execSQL("INSERT INTO Escuderia(escuderia) VALUES ('Red Bull')")
        db?.execSQL("INSERT INTO Escuderia(escuderia) VALUES ('ScuderiaFerrari')")
        db?.execSQL("INSERT INTO Escuderia(escuderia) VALUES ('McLarenF1Team')")
        db?.execSQL("INSERT INTO Escuderia(escuderia) VALUES ('AstonMartinAramcoF1Team')")
        db?.execSQL("INSERT INTO Escuderia(escuderia) VALUES ('WilliamsRacing')")

        // Tipos de neumáticos
        db?.execSQL("INSERT INTO TipoCambioNeumatico(tipo) VALUES ('Duro')")
        db?.execSQL("INSERT INTO TipoCambioNeumatico(tipo) VALUES ('Medio')")
        db?.execSQL("INSERT INTO TipoCambioNeumatico(tipo) VALUES ('Blando')")
        db?.execSQL("INSERT INTO TipoCambioNeumatico(tipo) VALUES ('Intermedios')")
        db?.execSQL("INSERT INTO TipoCambioNeumatico(tipo) VALUES ('Lluvia extrema')")
    }

    // Se ejecuta cuando se cambia la versión de la base de datos
    // Aquí se eliminan las tablas antiguas y se vuelven a crear
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS PitStop")
        db?.execSQL("DROP TABLE IF EXISTS Piloto")
        db?.execSQL("DROP TABLE IF EXISTS Escuderia")
        db?.execSQL("DROP TABLE IF EXISTS TipoCambioNeumatico")
        onCreate(db)
    }
}
