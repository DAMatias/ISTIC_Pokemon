package com.istic_pokemon.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

//Creamos un DataStore específico para los favoritos
private val Context.favoritesDataStore by preferencesDataStore(name = "favorites")

class FavoritesRepository(private val context: Context) {

    private val favoritesKey = stringPreferencesKey("favorite_pokemons")
    private val gson = Gson()

    suspend fun addFavorite(pokemonName: String) {
        val currentFavorites = getFavorites().toMutableList()
        if (!currentFavorites.contains(pokemonName)) {
            currentFavorites.add(pokemonName)
            saveFavorites(currentFavorites)
        }
    }

    suspend fun removeFavorite(pokemonName: String) {
        val currentFavorites = getFavorites().toMutableList()
        if (currentFavorites.remove(pokemonName)) {
            saveFavorites(currentFavorites)
        }
    }

    suspend fun getFavorites(): List<String> {
        val json = context.favoritesDataStore.data.map { preferences ->
            preferences[favoritesKey] ?: "[]" // Si está vacío, devuelve una lista vacía en JSON
        }.first()

        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }

    private suspend fun saveFavorites(favorites: List<String>) {
        val json = gson.toJson(favorites)
        context.favoritesDataStore.edit { preferences ->
            preferences[favoritesKey] = json
        }
    }
}