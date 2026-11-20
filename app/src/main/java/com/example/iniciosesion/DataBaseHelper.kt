package com.example.iniciosesion

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        private const val DATABASE_NAME = "booklingo.db"
        private const val DATABASE_VERSION = 2

        // Constantes para la tabla de usuarios
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"

        // Constantes para la nueva tabla de traducciones favoritas (datos del API)
        const val TABLE_FAVORITOS = "traducciones_favoritas"
        const val COL_FAVORITO_ID = "id"
        const val COL_TEXTO_ORIGINAL = "texto_original"
        const val COL_TEXTO_TRADUCIDO = "texto_traducido"
        const val COL_IDIOMAS = "idiomas"

        // Query SQL para crear la tabla de usuarios
        private const val CREATE_TABLE_USUARIOS = """
            CREATE TABLE $TABLE_USUARIOS(
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL UNIQUE,
                $COL_PASSWORD TEXT NOT NULL
            )
        """

        // Query SQL para crear la tabla de traducciones favoritas
        private const val CREATE_TABLE_FAVORITOS = """
            CREATE TABLE $TABLE_FAVORITOS(
                $COL_FAVORITO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TEXTO_ORIGINAL TEXT NOT NULL,
                $COL_TEXTO_TRADUCIDO TEXT NOT NULL,
                $COL_IDIOMAS TEXT NOT NULL
            )
        """
    }

    // METODO PARA CREAR LA BASE DE DATOS
    override fun onCreate(db: SQLiteDatabase?) {
        // Crear tabla de usuarios y tabla de favoritos
        db?.execSQL(CREATE_TABLE_USUARIOS)
        db?.execSQL(CREATE_TABLE_FAVORITOS)
    }

    // METODO PARA ACTUALIZAR LA BASE DE DATOS
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Verificar que la version anterior sea menor a 2 para crear la nueva tabla
        if (oldVersion < 2) {
            db?.execSQL(CREATE_TABLE_FAVORITOS)
        }
    }

    // Metodo para insertar registro de usuario
    fun registrarUsuario(nombre: String, correo: String, password: String): Boolean {
        // Crear variable para que la base de datos se pueda modificar
        val db = writableDatabase

        // Crear variable para recibir los datos que escribe el usuario
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }

        // Insertar datos en la tabla
        val resultado = db.insert(TABLE_USUARIOS, null, datos)

        // Cerrar la conexion de la base de datos
        db.close()

        // Comparar el valor del resultado de la insercion y regresarlo como boolean
        return resultado != -1L
    }

    // Metodo para consultar a un usuario
    fun IniciarSesion(correo: String, password: String): Boolean {
        val db = readableDatabase
        val query_sql = """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
        """.trimIndent()

        // Crear cursor para recorrer informacion
        val cursor = db.rawQuery(
            query_sql, arrayOf(correo, password)
        )

        // Crear variable de tipo bool que permita saber si el cursor encontro un registro
        val usuarioEncontrado = cursor.count > 0

        cursor.close()
        db.close()

        return usuarioEncontrado
    }

    // METODO PARA REGISTRAR INFORMACION PROVENIENTE DEL API (FAVORITOS)
    fun guardarTraduccionFavorita(
        textoOriginal: String,
        textoTraducido: String,
        idiomas: String
    ): Boolean {
        // Declarar el uso de la bd en modo escritura
        val db = writableDatabase

        // Empaquetar los datos relevantes de la traduccion
        val datos = ContentValues().apply {
            put(COL_TEXTO_ORIGINAL, textoOriginal)
            put(COL_TEXTO_TRADUCIDO, textoTraducido)
            put(COL_IDIOMAS, idiomas)
        }

        // Insercion en la tabla de favoritos
        val resultado = db.insertWithOnConflict(
            TABLE_FAVORITOS,
            null,
            datos,
            SQLiteDatabase.CONFLICT_IGNORE
        )

        db.close()
        return resultado != -1L
    }

    // METODO PARA ELIMINAR UN REGISTRO DE FAVORITOS
    fun eliminarTraduccionFavorita(textoOriginal: String, idiomas: String): Boolean {
        val db = writableDatabase

        // Eliminar el registro que coincida con el texto original y el par de idiomas
        val filasAfectadas = db.delete(
            TABLE_FAVORITOS,
            "$COL_TEXTO_ORIGINAL = ? AND $COL_IDIOMAS = ?",
            arrayOf(textoOriginal, idiomas)
        )

        db.close()
        return filasAfectadas > 0
    }

    // METODO PARA VERIFICAR QUE UN REGISTRO EXISTE EN FAVORITOS
    fun existeTraduccionFavorita(textoOriginal: String, idiomas: String): Boolean {
        val db = readableDatabase

        // Consultar si ya existe la traduccion guardada
        val cursor = db.rawQuery(
            "SELECT $COL_FAVORITO_ID FROM $TABLE_FAVORITOS WHERE $COL_TEXTO_ORIGINAL = ? AND $COL_IDIOMAS = ?",
            arrayOf(textoOriginal, idiomas)
        )

        // Verificar si el cursor encontro al menos un resultado
        val existe = cursor.moveToFirst()
        cursor.close()
        db.close()

        return existe
    }
}