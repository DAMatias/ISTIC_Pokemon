package com.istic_pokemon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.istic_pokemon.models.Pokemon
import com.istic_pokemon.repository.FavoritesRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesRepository = FavoritesRepository(application)

    private val _favoritesState = MutableLiveData<FavoritesState>()
    val favoritesState: LiveData<FavoritesState> = _favoritesState

    fun loadFavorites() {
        _favoritesState.value = FavoritesState.Loading
        viewModelScope.launch {
            try {
                //Traemos la lista de nombres desde DataStore
                val favoriteNames = favoritesRepository.getFavorites()

                if (favoriteNames.isEmpty()) {
                    _favoritesState.value = FavoritesState.Empty
                } else {
                    //Convertimos los nombres guardados en objetos para el Adapter
                    val pokemonList = favoriteNames.map { name ->
                        Pokemon(
                            name = name,
                            url = "https://pokeapi.co/api/v2/pokemon/$name/"
                        )
                    }
                    _favoritesState.value = FavoritesState.Success(pokemonList)
                }
            } catch (e: Exception) {
                _favoritesState.value = FavoritesState.Error(e.message ?: "Error al cargar favoritos")
            }
        }
    }

    sealed class FavoritesState {
        object Loading : FavoritesState()
        object Empty : FavoritesState()
        data class Success(val favorites: List<Pokemon>) : FavoritesState()
        data class Error(val message: String) : FavoritesState()
    }
}