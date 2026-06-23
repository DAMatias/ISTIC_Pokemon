package com.istic_pokemon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.istic_pokemon.models.Pokemon
import com.istic_pokemon.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class PokemonListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PokemonRepository()
    private var allPokemons: List<Pokemon> = emptyList()

    private val _pokemonListState = MutableLiveData<PokemonListState>()
    val pokemonListState: LiveData<PokemonListState> = _pokemonListState

    fun loadPokemons() { // Cambié a función normal, lanzamos corrutina acá
        _pokemonListState.value = PokemonListState.Loading
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    repository.getPokemons()
                }

                // Mapeamos los Pokémon para cargar sus tipos
                val updatedPokemons = result.map { pokemon ->
                    val detail = repository.getPokemonDetail(pokemon.name) // Llamada extra
                    // Unimos los tipos con coma
                    pokemon.typesStr = detail.types.joinToString(", ") { it.type.name }
                    pokemon
                }

                allPokemons = updatedPokemons
                _pokemonListState.value = PokemonListState.Success(allPokemons)

            } catch (e: Exception) {
                _pokemonListState.value = PokemonListState.Error("Error: ${e.message}")
            }
        }
    }

    suspend fun searchPokemons(query: String) {
        if (query.isEmpty()) {
            if (allPokemons.isNotEmpty()) {
                _pokemonListState.postValue(PokemonListState.Success(allPokemons))
            }
            return
        }

        val filtered = allPokemons.filter { pokemon ->
            pokemon.name.contains(query, ignoreCase = true)
        }

        if (filtered.isNotEmpty()) {
            _pokemonListState.postValue(PokemonListState.Success(filtered))
        } else {
            _pokemonListState.postValue(PokemonListState.Empty)
        }
    }

    sealed class PokemonListState {
        object Loading : PokemonListState()
        data class Success(val pokemons: List<Pokemon>) : PokemonListState()
        object Empty : PokemonListState()
        data class Error(val message: String) : PokemonListState()
    }
}