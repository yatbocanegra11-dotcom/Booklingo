package com.example.iniciosesion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.example.iniciosesion.model.TraduccionFavorita
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FavoritosActivity : AppCompatActivity() {

    private lateinit var contenedorFavoritos: LinearLayout
    private lateinit var tvSinFavoritos: TextView
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var dbHelper: DataBaseHelper
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favoritos)

        // Referencias a componentes visuales
        contenedorFavoritos = findViewById(R.id.contenedorFavoritos)
        tvSinFavoritos = findViewById(R.id.tvSinFavoritos)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Inicializar base de datos y autenticacion
        dbHelper = DataBaseHelper(this)
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        // Cargar favoritos y configurar navegacion inferior
        cargarFavoritos()
        menuNavegacion()
    }

    override fun onResume() {
        super.onResume()
        // Refrescar la lista cada vez que se vuelva a mostrar la pantalla
        cargarFavoritos()
    }

    // Metodo para consultar y mostrar todas las traducciones favoritas guardadas
    private fun cargarFavoritos() {
        contenedorFavoritos.removeAllViews()
        val favoritos = dbHelper.obtenerTraduccionesFavoritas()

        // Mostrar mensaje si no hay traducciones guardadas
        if (favoritos.isEmpty()) {
            tvSinFavoritos.visibility = View.VISIBLE
            return
        }

        tvSinFavoritos.visibility = View.GONE

        // Crear una tarjeta por cada traduccion favorita encontrada
        for (item in favoritos) {
            val vistaFavorito = layoutInflater.inflate(
                R.layout.item_favorito,
                contenedorFavoritos,
                false
            )

            val tvIdiomas = vistaFavorito.findViewById<TextView>(R.id.tvItemIdiomas)
            val tvOriginal = vistaFavorito.findViewById<TextView>(R.id.tvItemOriginal)
            val tvTraducido = vistaFavorito.findViewById<TextView>(R.id.tvItemTraducido)

            tvIdiomas.text = item.idiomas
            tvOriginal.text = item.textoOriginal
            tvTraducido.text = item.textoTraducido

            // Al hacer clic en cualquier favorito, preguntar si desea quitarlo
            vistaFavorito.setOnClickListener {
                confirmarEliminarFavorito(item)
            }

            contenedorFavoritos.addView(vistaFavorito)
        }
    }

    // Metodo para preguntar si el usuario desea quitar el favorito seleccionado
    private fun confirmarEliminarFavorito(favorito: TraduccionFavorita) {
        AlertDialog.Builder(this)
            .setTitle("Quitar de favoritos")
            .setMessage("¿Deseas quitar \"${favorito.textoOriginal}\" de favoritos?")
            .setPositiveButton("Sí, quitar") { _, _ ->
                val eliminado = dbHelper.eliminarTraduccionFavorita(
                    textoOriginal = favorito.textoOriginal,
                    idiomas = favorito.idiomas
                )

                if (eliminado) {
                    Toast.makeText(
                        this,
                        "Traducción quitada de favoritos",
                        Toast.LENGTH_SHORT
                    ).show()
                    cargarFavoritos()
                } else {
                    Toast.makeText(
                        this,
                        "No se pudo quitar la traducción",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Metodo para el funcionamiento del menu inferior
    private fun menuNavegacion() {
        bottomNavigation.selectedItemId = R.id.nav_favoritos

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    val intent = Intent(this@FavoritosActivity, PrincipalActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_favoritos -> {
                    true
                }

                R.id.nav_salir -> {
                    cerrarSesionGoogle()
                    true
                }

                else -> false
            }
        }
    }

    // Metodo para cerrar sesion y limpiar credenciales
    private fun cerrarSesionGoogle() {
        auth.signOut()
        lifecycleScope.launch {
            try {
                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest()
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this@FavoritosActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            val intent = Intent(this@FavoritosActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}