package com.obu.restaurant.presentation.activities

import DatabaseService
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.obu.restaurant.data.database.UserDao
import com.obu.restaurant.data.repository.implementations.UserRepository
import com.obu.restaurant.databinding.ActivityLoginBinding
import com.obu.restaurant.domain.usecases.LoginUseCase
import com.obu.restaurant.presentation.viewmodels.LoginViewModel
import com.obu.restaurant.presentation.viewmodels.LoginViewModelFactory
import com.obu.restaurant.utils.DialogUtils

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout con View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Crear dependencias manualmente
        val databaseService = DatabaseService() // Instancia de DatabaseService
        val userDao = UserDao(databaseService)  // Instancia de UserDao
        val userRepository = UserRepository(userDao) // Instancia de UserRepository
        val loginUseCase = LoginUseCase(userRepository) // Instancia de LoginUseCase

        // Crear el LoginViewModel usando la Factory
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory(loginUseCase))
            .get(LoginViewModel::class.java)

        // Observador para el estado del login
        loginViewModel.loginResult.observe(this, Observer { result ->
            binding.progressBar.visibility = View.GONE

            result.fold(
                onSuccess = { user ->
                    if (user.rol==1 || user.rol ==3){
                        // Usuario autenticado exitosamente
                        DialogUtils.showDialog(
                            context = this,
                            message = "Bienvenido, ${user.des_nom}",
                            dialogType = DialogUtils.DialogType.INFO
                        )
                        val sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)  // Indica que el usuario ha iniciado sesión
                        editor.putString("userId", user.user)  // Puedes almacenar el ID del mozo si es necesario
                        editor.apply()
                        navigateToTableList()
                    }else {
                        DialogUtils.showDialog(
                            context = this,
                            message = "App para perfil Mozo o Admin.",
                            dialogType = DialogUtils.DialogType.INFO
                        )
                    }
                },
                onFailure = { exception ->
                    val errorMessage = exception.message ?: "Error inesperado, intente nuevamente."
                    DialogUtils.showDialog(
                        context = this,
                        message = errorMessage,  // Aquí pasamos el mensaje de la excepción
                        dialogType = DialogUtils.DialogType.ERROR
                    )
                }
            )
        })

        // Configurar el botón de login
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString().lowercase()
            val password = binding.editTextPassword.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                binding.progressBar.visibility = View.VISIBLE

                loginViewModel.login(username, password)
            } else {
                DialogUtils.showDialog(
                    context = this,
                    message = "Por favor, ingrese usuario y contraseña.",
                    dialogType = DialogUtils.DialogType.INFO
                )
            }
        }
    }

    private fun navigateToTableList() {
        val intent = Intent(this, MesaActivity::class.java)
        startActivity(intent)
        finish()  // Finalizar LoginActivity para que no regrese al login al presionar 'Back'
    }

}
