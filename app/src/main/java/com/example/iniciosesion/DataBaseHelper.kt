package com.example.iniciosesion

import android.R
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf

class DataBaseHelper(context: Context) : SQLiteOpenHelper(
        context,
        DATABASE_NAME,
    null,
        DATABASE_VERSION,

        ){

    companion object{
        private const val DATABASE_NAME = "musica.db"
        private const val  DATABASE_VERSION = 1
        private const val TABLE_USUARIOS = "usuarios"
        private const val  COL_ID = "id"
        private const val  COL_NOMBRE = "nombre"
        private const val  COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"

    }
    //METODO PARA CREAR LA BASE DE DATOS

    override fun onCreate(db: SQLiteDatabase?) {
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS(
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_NOMBRE TEXT  NOT NULL,
            $COL_CORREO TEXT NOT NULL UNIQUE,
            $COL_PASSWORD TEXT NOT NULL
            )



        """.trimIndent()
        //CREAR TABLA DE SQL
        db?.execSQL(crearTablaUsuarios)
    }

    //METODO PARA ACTUALIZAR LA VASE DE DATOS
    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    //metodo para insertar registro
    fun registrarUsuario (nombre: String, correo : String, password : String ): Boolean {
        //crear variable para que a base de datos se pueda modificar
        val db = writableDatabase

        //craer variable para recibir los datos que escribe el usuario
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }
        //INSERTAR DATOS EN LA TABLA

        val resultado = db.insert(TABLE_USUARIOS, null, datos)

        //crear la conexion de la base de datos
        db.close()

        //comparar el valor del resultado de la insercion y agregarlo como boolean
        return resultado != -1L




        }
    //metodo pra consultar a un usuario
    fun IniciarSesion(correo : String, password: String) : Boolean {
        val db = readableDatabase
        val query_sql = """
            SELECT * FROM $TABLE_USUARIOS
        WHERE $COL_CORREO = ? AND $COL_PASSWORD = ? 
        """.trimIndent()

        //crear cursor pra recorrer informacion
        val cursor = db.rawQuery(
            query_sql, arrayOf(correo, password)
        )

        //crear variable de tipo bool que permita saber si el cursor encontro un registro
        val usuarioEncontrado = cursor.count > 0

        cursor.close()
        db.close()

        return usuarioEncontrado


        }

    }