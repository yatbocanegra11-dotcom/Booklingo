package com.example.iniciosesion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.iniciosesion.model.TraduccionResponse
import com.example.iniciosesion.network.RetrofitClient

class PrincipalActivity : AppCompatActivity() {



    private lateinit var etTexto: EditText
    private lateinit var btnTraducir: Button
    private lateinit var tvResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)

        etTexto = findViewById(R.id.etTexto)
        btnTraducir = findViewById(R.id.btnTraducir)
        tvResultado = findViewById(R.id.tvResultado)

        btnTraducir.setOnClickListener {
            traducirTexto()
        }
    }

    // Método para traducir el texto escrito por el usuario
    private fun traducirTexto() {
        val texto = etTexto.text.toString().trim()

        if (texto.isEmpty()) {
            Toast.makeText(
                this@PrincipalActivity,
                "Escribe una frase en inglés",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        tvResultado.text = "Traduciendo..."

        RetrofitClient.api.traducirTexto(
            texto = texto,
            idiomas = "en|es"
        ).enqueue(object : Callback<TraduccionResponse> {

            // Método para recibir la respuesta de la API
            override fun onResponse(
                call: Call<TraduccionResponse>,
                response: Response<TraduccionResponse>
            ) {
                if (response.isSuccessful) {
                    val traduccion = response.body()
                        ?.responseData?.translatedText

                    if (!traduccion.isNullOrBlank()) {
                        tvResultado.text = "Traducción:\n$traduccion"
                    } else {
                        tvResultado.text = "No se encontró una traducción."
                    }
                } else {
                    tvResultado.text = "No se pudo realizar la traducción."
                }
            }

            // Método para manejar errores de conexión
            override fun onFailure(
                call: Call<TraduccionResponse>,
                t: Throwable
            ) {
                tvResultado.text = "Error al conectar con la API."
                Toast.makeText(this@PrincippalActivity,"Verifica tu conexión a internet",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
