package com.istic_pokemon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope // Faltaba este import
import com.istic_pokemon.models.PokemonDetail
import com.istic_pokemon.repository.FavoritesRepository
import com.istic_pokemon.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch // Faltaba este import
import kotlinx.coroutines.withContext

class PokemonDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PokemonRepository()
    private val favoritesRepository = FavoritesRepository(application)

    private val _pokemonDetailState = MutableLiveData<PokemonDetailState>()
    val pokemonDetailState: LiveData<PokemonDetailState> = _pokemonDetailState

    private val _isFavorite = MutableStateFlow<Boolean>(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    // CORRECCIÓN 1: Agregamos la variable que faltaba
    private var currentPokemonName: String? = null

    suspend fun loadPokemonDetail(name: String) {
        currentPokemonName = name // Guardamos el nombre para usarlo en el botón después
        _pokemonDetailState.postValue(PokemonDetailState.Loading)
        try {
            // CORRECCIÓN 2: Separamos la validación para que 'result' guarde al Pokémon
            val result = withContext(Dispatchers.IO) {
                repository.getPokemonDetail(name)
            }
            checkIfFavorite(name)
            _pokemonDetailState.postValue(PokemonDetailState.Success(result))
        } catch (e: Exception) {
            _pokemonDetailState.postValue(
                PokemonDetailState.Error("Error al cargar detalle: ${e.message}")
            )
        }
    }

    private fun checkIfFavorite(name: String) {
        viewModelScope.launch {
            val favorites = favoritesRepository.getFavorites()
            _isFavorite.value = favorites.contains(name)
        }
    }

    fun toggleFavorite() {
        val name = currentPokemonName ?: return
        viewModelScope.launch {
            if (_isFavorite.value) {
                favoritesRepository.removeFavorite(name)
                _isFavorite.value = false
            } else {
                favoritesRepository.addFavorite(name)
                _isFavorite.value = true
            }
        }
    }

    sealed class PokemonDetailState {
        object Loading : PokemonDetailState()
        data class Success(val pokemonDetail: PokemonDetail) : PokemonDetailState()
        data class Error(val message: String) : PokemonDetailState()
    }
}