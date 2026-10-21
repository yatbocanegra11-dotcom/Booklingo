package com.example.iniciosesion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo : EditText
    private lateinit var etPass : EditText
    private lateinit var btnInicio : Button
    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        //referiencia a componentes
        etCorreo = findViewById(R.id.etCorreo)
        etPass  = findViewById(R.id.etpassword)
        btnInicio = findViewById(R.id.btnIngresar)

        dbHelper = DataBaseHelper(this)

        btnInicio.setOnClickListener {
            //TODO: AGREGAR METODO DE INCIO DE SESION
            iniciarSesion()
        }

        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegistrarActivity3::class.java)
            startActivity(intent)
        }

    }
    //METODO PARA INICIAR SESION

    private fun iniciarSesion() {

        val correo = etCorreo.text.toString().trim()
        val pass = etPass.text.toString().trim()

        // Verificar que los campos tengan información
        if (correo.isEmpty() || pass.isEmpty()) {
            Toast.makeText(
                this,
                "Completa todos los campos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            // Consultar al usuario en la base de datos
            val resultado = dbHelper.IniciarSesion(correo, pass)

            // Comprobar el resultado
            if (resultado) {

                Toast.makeText(
                    this,
                    "BIENVENIDO",
                    Toast.LENGTH_SHORT
                ).show()

                // Abrir la pantalla principal
                val intent = Intent(this, PrincipalActivity::class.java)
                startActivity(intent)

            } else {

                Toast.makeText(
                    this,
                    "Correo o contraseña incorrectos",
                    Toast.LENGTH_LONG
                ).show()
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()

            android.util.Log.e("INICIO_SESION", "Error al iniciar sesión", e)
        }
    }

}