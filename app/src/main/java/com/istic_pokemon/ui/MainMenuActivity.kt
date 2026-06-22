package com.istic_pokemon.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.istic_pokemon.R
import com.istic_pokemon.databinding.ActivityMainMenuBinding
import com.istic_pokemon.viewmodel.MainMenuViewModel
import kotlinx.coroutines.launch

class MainMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainMenuBinding
    private val mainMenuViewModel: MainMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Menú Principal"

        //Mostrar nombre dentro de corrutina
        lifecycleScope.launch {
            val username = mainMenuViewModel.getUsername()
            binding.tvWelcome.text = "Bienvenido, $username!"
        }

        //Configurar cards de navegación
        setupNavigationCards()

        //Configurar botón de logout
        binding.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                mainMenuViewModel.logout()
                navigateToLogin()
            }
        }
    }

    private fun setupNavigationCards() {
        binding.cardPokemons.setOnClickListener {
            startActivity(Intent(this, PokemonListActivity::class.java))
        }

        binding.cardFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                lifecycleScope.launch {
                    mainMenuViewModel.logout()
                    navigateToLogin()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainMenuActivity::class.java)
        }
    }
}