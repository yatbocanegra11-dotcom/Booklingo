package com.example.iniciosesion

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_guardar_traduccion)

        tvIdiomas = findViewById(R.id.tvIdiomas)
        tvTextoOriginal = findViewById(R.id.tvTextoOriginal)
        tvTextoTraducido = findViewById(R.id.tvTextoTraducido)
        btnGuardar = findViewById(R.id.btnGuardar)
        btnRegresar = findViewById(R.id.btnRegresar)

        mostrarInformacionTraduccion()

        btnGuardar.setOnClickListener {
            Toast.makeText(
                this,
                "La informacion se guardo",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnRegresar.setOnClickListener {
            finish()
        }
    }

    // Metodo para obtener informacion
    private fun mostrarInformacionTraduccion() {

        val textoOriginal = intent.getStringExtra("textoOriginal")
        val textoTraducido = intent.getStringExtra("textoTraducido")
        val idiomas = intent.getStringExtra("idiomas")

        tvIdiomas.text = idiomas
        tvTextoOriginal.text = textoOriginal
        tvTextoTraducido.text = textoTraducido
    }
}
