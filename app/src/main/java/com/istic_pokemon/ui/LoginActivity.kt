package com.istic_pokemon.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.istic_pokemon.R
import com.istic_pokemon.databinding.ActivityLoginBinding
import com.istic_pokemon.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ CORREGIDO: Verificar sesión dentro de corrutina
        lifecycleScope.launch {
            loginViewModel.checkSession()
        }

        // Observar estado del login
        loginViewModel.loginState.observe(this) { state ->
            when (state) {
                is LoginViewModel.LoginState.Loading -> {
                    binding.progressBarLogin.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is LoginViewModel.LoginState.Success -> {
                    binding.progressBarLogin.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    navigateToMainMenu()
                }
                is LoginViewModel.LoginState.Error -> {
                    binding.progressBarLogin.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.LoginState.SessionActive -> {
                    navigateToMainMenu()
                }
                LoginViewModel.LoginState.Idle -> {
                    binding.progressBarLogin.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                }
            }
        }

        // Configurar botón de login
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val rememberSession = binding.cbRemember.isChecked

            if (validateFields(username, password)) {
                // ✅ CORREGIDO: Login dentro de corrutina
                lifecycleScope.launch {
                    loginViewModel.login(username, password, rememberSession)
                }
            }
        }
    }

    private fun validateFields(username: String, password: String): Boolean {
        return when {
            username.isEmpty() -> {
                binding.etUsername.error = "El usuario es requerido"
                false
            }
            password.isEmpty() -> {
                binding.etPassword.error = "La contraseña es requerida"
                false
            }
            else -> true
        }
    }

    private fun navigateToMainMenu() {
        startActivity(MainMenuActivity.newIntent(this))
        finish()
    }

    companion object {
        fun newIntent(context: android.content.Context): android.content.Intent {
            return android.content.Intent(context, LoginActivity::class.java)
        }
    }
}