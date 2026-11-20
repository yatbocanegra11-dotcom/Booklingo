package com.example.iniciosesion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo : EditText
    private lateinit var etPass : EditText
    private lateinit var btnInicio : Button
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var btnGoogle: ImageButton

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

        btnGoogle = findViewById(R.id.btnGoogle)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        btnInicio.setOnClickListener {
            //TODO: AGREGAR METODO DE INCIO DE SESION
            iniciarSesion()
        }

        btnGoogle.setOnClickListener {
            iniciarSesionGoogle()
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
    private fun autenticarFirebase(idToken: String) {

        val credencialFirebase = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencialFirebase).addOnCompleteListener(this) {

                inicio ->

            //validar si el inicio de sesion con firebase es exitoso
            if (inicio.isSuccessful) {
                //variable para obtener toda la info del usuario
                val usuario = auth.currentUser
                Toast.makeText(this, "Bienvenido ${usuario?.displayName}", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, PrincipalActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Error al validar con Firebase", Toast.LENGTH_SHORT).show()

            }

        }

    }
    private fun procesarCredencial(credential: Credential) {

        if (credential is CustomCredential && credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {

            try {
                //variable para obtener la credencial de google
                val credencialGoogle = GoogleIdTokenCredential.createFrom(
                    credential.data
                )
                autenticarFirebase(credencialGoogle.idToken)

            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(this, "Credenciales invalida!", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun iniciarSesionGoogle() {

        val inicioGoogle = GetSignInWithGoogleOption.Builder(
            getString(R.string.default_web_client_id)
        ).build()

        val solicitud = GetCredentialRequest.Builder()
            .addCredentialOption(inicioGoogle)
            .build()

        lifecycleScope.launch {
            try {
                //validar que el resultado del inicio sea correcto
                val resultado = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = solicitud
                )
                procesarCredencial(resultado.credential)

            } catch (e: GetCredentialException) {
                Toast.makeText(this@MainActivity, "Error al iniciar sesion", Toast.LENGTH_SHORT)
                    .show()

            }
        }


    }
    override fun onStart() {
        super.onStart()

        val usuarioctual = auth.currentUser
        if (usuarioctual != null) {
            val intent = Intent(this, PrincipalActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}