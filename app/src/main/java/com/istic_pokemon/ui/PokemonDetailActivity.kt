package com.istic_pokemon.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.istic_pokemon.R
import com.istic_pokemon.databinding.ActivityPokemonDetailBinding
import com.istic_pokemon.viewmodel.PokemonDetailViewModel
import kotlinx.coroutines.launch

class PokemonDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPokemonDetailBinding
    private val viewModel: PokemonDetailViewModel by viewModels()
    private var pokemonName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener nombre del Pokémon
        pokemonName = intent.getStringExtra(EXTRA_POKEMON_NAME) ?: ""

        // Configurar Toolbar
        setSupportActionBar(binding.toolbarDetail)
        supportActionBar?.title = "Detalle"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Observar estado del detalle
        viewModel.pokemonDetailState.observe(this) { state ->
            when (state) {
                is PokemonDetailViewModel.PokemonDetailState.Loading -> {
                    // Mostrar loading
                }
                is PokemonDetailViewModel.PokemonDetailState.Success -> {
                    displayPokemonDetail(state)
                }
                is PokemonDetailViewModel.PokemonDetailState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.isFavorite.collect { favorite ->
                val btn = binding.btnFavorite as com.google.android.material.button.MaterialButton

                if (favorite) {
                    btn.setIconResource(R.drawable.ic_favorite)
                    btn.setIconTint(android.content.res.ColorStateList.valueOf(getColor(R.color.purple_500)))
                    btn.text = "En Favoritos"
                } else {
                    btn.setIconResource(R.drawable.ic_favorite)
                    btn.setIconTint(android.content.res.ColorStateList.valueOf(getColor(android.R.color.darker_gray)))
                    btn.text = "Agregar a Favoritos"
                }
            }
        }

        // Cargar detalle dentro de una corrutina
        if (pokemonName.isNotEmpty()) {
            lifecycleScope.launch {
                viewModel.loadPokemonDetail(pokemonName)
            }
        } else {
            Toast.makeText(this, "Error: Pokémon no especificado", Toast.LENGTH_SHORT).show()
            finish()
        }
        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }
    }

    private fun displayPokemonDetail(state: PokemonDetailViewModel.PokemonDetailState.Success) {
        val pokemon = state.pokemonDetail

        // Nombre
        binding.tvPokemonName.text = pokemon.name.replaceFirstChar { it.uppercase() }

        //Tipos
        val types = pokemon.types.joinToString(", ") { it.type.name }
        binding.tvPokemonTypes.text = "Tipos: $types"

        //Estadísticas
        pokemon.stats.forEach { stat ->
            when (stat.stat.name) {
                "hp" -> {
                    binding.pbHp.progress = stat.baseStat
                    binding.tvHpValue.text = stat.baseStat.toString()
                }
                "attack" -> {
                    binding.pbAttack.progress = stat.baseStat
                    binding.tvAttackValue.text = stat.baseStat.toString()
                }
                "defense" -> {
                    binding.pbDefense.progress = stat.baseStat
                    binding.tvDefenseValue.text = stat.baseStat.toString()
                }
                "speed" -> {
                    binding.pbSpeed.progress = stat.baseStat
                    binding.tvSpeedValue.text = stat.baseStat.toString()
                }
            }
        }

        // Cargar imagen
        val imageUrl = pokemon.sprites.other?.officialArtwork?.frontDefault
            ?: pokemon.sprites.frontDefault
        if (!imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_pokeball)
                .error(R.drawable.ic_pokeball)
                .into(binding.ivPokemonImage)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    companion object {
        private const val EXTRA_POKEMON_NAME = "extra_pokemon_name"

        fun newIntent(context: Context, pokemonName: String): Intent {
            return Intent(context, PokemonDetailActivity::class.java).apply {
                putExtra(EXTRA_POKEMON_NAME, pokemonName)
            }
        }
    }
}