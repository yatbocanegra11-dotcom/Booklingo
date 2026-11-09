package com.example.iniciosesion

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.iniciosesion.model.TraduccionResponse
import com.example.iniciosesion.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrincipalActivity : AppCompatActivity() {

    private lateinit var etTexto: EditText
    private lateinit var btnTraducir: Button
    private lateinit var tvResultado: TextView
    private lateinit var spIdiomas: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)

        etTexto = findViewById(R.id.etTexto)
        btnTraducir = findViewById(R.id.btnTraducir)
        tvResultado = findViewById(R.id.tvResultado)
        spIdiomas = findViewById(R.id.spIdiomas)

        // Opciones de idiomas disponibles
        val opcionesIdiomas = arrayOf(
            "English → Español",
            "Español → English",
            "English → Français"
        )

        // adaptar para mostrar las opciones en el Spinner
        val adaptador = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            opcionesIdiomas
        )

        adaptador.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spIdiomas.adapter = adaptador

        btnTraducir.setOnClickListener {
            traducirTexto()
        }
    }

    // Metodo para traducir el texto escrito por el usuario
    private fun traducirTexto() {
        val texto = etTexto.text.toString().trim()

        if (texto.isEmpty()) {
            Toast.makeText(
                this@PrincipalActivity,
                "Escribe un texto para traducir",Toast.LENGTH_SHORT).show()
            return
        }

        // Obtener el idioma seleccionado
        val idiomas = when (spIdiomas.selectedItemPosition) {
            0 -> "en|es"
            1 -> "es|en"
            2 -> "en|fr"
            else -> "en|es"
        }

        tvResultado.text = "Traduciendo..."

        RetrofitClient.api.traducirTexto(
            texto = texto,
            idiomas = idiomas
        ).enqueue(object : Callback<TraduccionResponse> {

            // Metodo para recibir la respuesta de la API
            override fun onResponse(
                call: Call<TraduccionResponse>,
                response: Response<TraduccionResponse>
            ) {
                if (response.isSuccessful) {
                    val traduccion = response.body()
                        ?.responseData?.translatedText

                    if (!traduccion.isNullOrBlank()) {
                        tvResultado.text = "Traduccion:\n$traduccion"
                    } else {
                        tvResultado.text = "No se encontro una traduccion."
                    }
                } else {
                    tvResultado.text = "No se pudo realizar la traduccion."
                }
            }

            // Metodo para manejar errores de conexion
            override fun onFailure(
                call: Call<TraduccionResponse>,
                t: Throwable
            ) {
                tvResultado.text = "Error al conectar con la API."

                Toast.makeText(
                    this@PrincipalActivity,
                    "Verifica tu conexion a internet",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}