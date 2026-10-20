package com.example.iniciosesion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarActivity3 : AppCompatActivity() {

    private lateinit var etNombre : EditText
    private lateinit var etCorreo : EditText
    private lateinit var etPassword : EditText

    private lateinit var btnRegistrar : Button

    //crear objeto de la clase de la base de datos
    private lateinit var dbHelper: DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar)


        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etpassword)
        btnRegistrar = findViewById(R.id.btnRegistrar)

        //Iniciar el objeto de la base de datos

        dbHelper= DataBaseHelper(this)

        //evento para regisrar un usuario
        btnRegistrar.setOnClickListener {
            //llamar al metodo de registrar usuario
            registrarUsuario()
        }


    }
    //metodo para registrar a un usuario
    private fun registrarUsuario(){
        //OBTENER LA INFORMACION QUE ESCRIBE EL USUARIO
        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val password = etPassword.text.toString().trim()

        //validar q los cqampos tengan informacion
        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty())
            Toast.makeText(this, "completa todos los campos", Toast.LENGTH_SHORT).show()

        //llamar a nuestra base de dato y guardar el resultado de la insercion
        val resultado = dbHelper.registrarUsuario(nombre, correo, password)

        if (resultado){
            //si el resutado es verdadero
            Toast.makeText(this, "REGISTRO EXITOSO", Toast.LENGTH_SHORT).show()
            finish()
        }else{
            Toast.makeText(this, "REGISTRO FALLADO", Toast.LENGTH_SHORT).show()

        }



    }
}