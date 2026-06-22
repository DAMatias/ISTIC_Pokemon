package com.istic_pokemon.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.istic_pokemon.R
import com.istic_pokemon.adapters.PokemonAdapter
import com.istic_pokemon.databinding.ActivityPokemonListBinding
import com.istic_pokemon.viewmodel.PokemonListViewModel
import kotlinx.coroutines.launch

class PokemonListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPokemonListBinding
    private lateinit var adapter: PokemonAdapter
    private val viewModel: PokemonListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPokemonListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Pokédex"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configurar RecyclerView
        setupRecyclerView()

        // Observar estado de la lista
        viewModel.pokemonListState.observe(this) { state ->
            when (state) {
                is PokemonListViewModel.PokemonListState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is PokemonListViewModel.PokemonListState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.updateList(state.pokemons)
                }
                is PokemonListViewModel.PokemonListState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                is PokemonListViewModel.PokemonListState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "No se encontraron Pokémon", Toast.LENGTH_SHORT).show()
                    adapter.updateList(emptyList())
                }
            }
        }

        // Configurar búsqueda
        setupSearch()

        // ✅ CORREGIDO: Cargar datos iniciales dentro de una corrutina
        lifecycleScope.launch {
            viewModel.loadPokemons()
        }
    }

    private fun setupRecyclerView() {
        adapter = PokemonAdapter(
            pokemonList = emptyList(), // Le pasamos la lista vacía al iniciar
            onItemClick = { pokemon ->
                startActivity(PokemonDetailActivity.newIntent(this, pokemon.name))
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // ✅ CORREGIDO: Búsqueda dentro de corrutina
                lifecycleScope.launch {
                    viewModel.searchPokemons(s?.toString()?.trim() ?: "")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}