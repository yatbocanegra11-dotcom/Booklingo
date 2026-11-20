package com.example.iniciosesion

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class GuardarTraduccionActivity : AppCompatActivity() {

    private lateinit var tvIdiomas: TextView
    private lateinit var tvTextoOriginal: TextView
    private lateinit var tvTextoTraducido: TextView
    private lateinit var btnGuardar: Button
    private lateinit var btnRegresar: Button

    // Bandera para saber si la traduccion ya esta en favoritos
    private var traduccionFavorita = false

    // Objeto para acceder a la base de datos
    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guardar_traduccion)

        tvIdiomas = findViewById(R.id.tvIdiomas)
        tvTextoOriginal = findViewById(R.id.tvTextoOriginal)
        tvTextoTraducido = findViewById(R.id.tvTextoTraducido)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnRegresar = findViewById(R.id.btnRegresar)

        // Inicializar la base de datos
        dbHelper = DataBaseHelper(this)

        // Mostrar los datos recibidos y configurar el boton
        mostrarInformacionTraduccion()

        btnRegresar.setOnClickListener {
            finish()
        }
    }

    // Metodo para obtener informacion y manejar favoritos
    private fun mostrarInformacionTraduccion() {

        // Recibir los datos enviados desde PrincipalActivity
        val textoOriginal = intent.getStringExtra("textoOriginal") ?: "DESCONOCIDO"
        val textoTraducido = intent.getStringExtra("textoTraducido") ?: "DESCONOCIDO"
        val idiomas = intent.getStringExtra("idiomas") ?: "DESCONOCIDO"

        // Mostrar la informacion en pantalla
        tvIdiomas.text = idiomas
        tvTextoOriginal.text = textoOriginal
        tvTextoTraducido.text = textoTraducido

        // Validacion para verificar si la traduccion ya existe en favoritos
        if (textoOriginal != "DESCONOCIDO" && idiomas != "DESCONOCIDO") {
            traduccionFavorita = dbHelper.existeTraduccionFavorita(textoOriginal, idiomas)
            actualizarTextoBoton()
        }

        // Evento para guardar o eliminar la traduccion al presionar el boton
        btnGuardar.setOnClickListener {
            if (traduccionFavorita) {
                // Si ya existe en favoritos, se elimina de la base de datos
                val eliminado = dbHelper.eliminarTraduccionFavorita(textoOriginal, idiomas)

                if (eliminado) {
                    traduccionFavorita = false
                    actualizarTextoBoton()
                    Toast.makeText(
                        this,
                        "Traduccion eliminada de favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Si no existe en favoritos, se registra en la base de datos
                val guardado = dbHelper.guardarTraduccionFavorita(
                    textoOriginal = textoOriginal,
                    textoTraducido = textoTraducido,
                    idiomas = idiomas
                )

                if (guardado) {
                    traduccionFavorita = true
                    actualizarTextoBoton()
                    Toast.makeText(
                        this,
                        "Traduccion agregada a favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Metodo para cambiar el texto y color del boton segun el estado de favorito
    private fun actualizarTextoBoton() {
        if (traduccionFavorita) {
            btnGuardar.text = "Eliminar de Favoritos"
            btnGuardar.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#E57373")
            )
        } else {
            btnGuardar.text = "Guardar traduccion"
            btnGuardar.backgroundTintList = ColorStateList.valueOf(
                Color.parseColor("#A8E6CF")
            )
        }
    }
}