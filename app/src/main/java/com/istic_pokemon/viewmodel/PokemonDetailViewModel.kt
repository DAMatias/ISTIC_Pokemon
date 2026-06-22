package com.istic_pokemon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.istic_pokemon.models.PokemonDetail
import com.istic_pokemon.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PokemonDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PokemonRepository()

    private val _pokemonDetailState = MutableLiveData<PokemonDetailState>()
    val pokemonDetailState: LiveData<PokemonDetailState> = _pokemonDetailState

    suspend fun loadPokemonDetail(name: String) {
        _pokemonDetailState.postValue(PokemonDetailState.Loading)
        try {
            val result = withContext(Dispatchers.IO) {
                repository.getPokemonDetail(name)
            }
            _pokemonDetailState.postValue(PokemonDetailState.Success(result))
        } catch (e: Exception) {
            _pokemonDetailState.postValue(
                PokemonDetailState.Error("Error al cargar detalle: ${e.message}")
            )
        }
    }

    sealed class PokemonDetailState {
        object Loading : PokemonDetailState()
        data class Success(val pokemonDetail: PokemonDetail) : PokemonDetailState()
        data class Error(val message: String) : PokemonDetailState()
    }
}