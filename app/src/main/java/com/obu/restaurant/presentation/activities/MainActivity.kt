package com.obu.restaurant.presentation.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.obu.restaurant.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener SharedPreferences
        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        // Verificar si el usuario ya está autenticado
        if (isLoggedIn) {
            // Si está autenticado, redirigir a la bandeja principal del mozo
            val intent = Intent(this, MesaActivity::class.java)
            startActivity(intent)
        } else {
            // Si no está autenticado, redirigir a la pantalla de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}
