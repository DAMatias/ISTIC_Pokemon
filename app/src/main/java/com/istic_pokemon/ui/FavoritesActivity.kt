package com.istic_pokemon.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.istic_pokemon.adapters.PokemonAdapter
import com.istic_pokemon.databinding.ActivityFavoritesBinding
import com.istic_pokemon.viewmodel.FavoritesViewModel

class FavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoritesBinding
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Configurar la barra superior (Toolbar)
        setSupportActionBar(binding.toolbarFavorites)
        supportActionBar?.title = "Mis Favoritos"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //Configurar el RecyclerView reutilizando tu PokemonAdapter
        adapter = PokemonAdapter(emptyList()) { pokemon -> startActivity(PokemonDetailActivity.newIntent
            (this, pokemon.name)) }//Al hacer clic, abrimos el detalle del Pokémon
        binding.rvFavorites.adapter = adapter

        //Observar el estado de los favoritos
        viewModel.favoritesState.observe(this) { state ->
            when (state) {
                is FavoritesViewModel.FavoritesState.Loading -> {
                    binding.progressBarFavorites.visibility = View.VISIBLE
                    binding.rvFavorites.visibility = View.GONE
                    binding.tvEmptyFavorites.visibility = View.GONE
                }
                is FavoritesViewModel.FavoritesState.Empty -> {
                    binding.progressBarFavorites.visibility = View.GONE
                    binding.rvFavorites.visibility = View.GONE
                    binding.tvEmptyFavorites.visibility = View.VISIBLE
                }
                is FavoritesViewModel.FavoritesState.Success -> {
                    binding.progressBarFavorites.visibility = View.GONE
                    binding.rvFavorites.visibility = View.VISIBLE
                    binding.tvEmptyFavorites.visibility = View.GONE
                    adapter.updateList(state.favorites)
                }
                is FavoritesViewModel.FavoritesState.Error -> {
                    binding.progressBarFavorites.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //Recarga la lista cada vez que la pantalla vuelve a estar visible
    override fun onResume() {
        super.onResume()
        viewModel.loadFavorites()
    }

    //Manejar el botón de volver atrás en la Toolbar
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}