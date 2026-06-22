package com.istic_pokemon.repository

import com.istic_pokemon.models.Pokemon
import com.istic_pokemon.models.PokemonDetail
import com.istic_pokemon.models.api.RetrofitClient
import retrofit2.HttpException
import java.io.IOException

class PokemonRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun getPokemons(): List<Pokemon> {
        return try {
            val response = apiService.getPokemons()
            if (response.isSuccessful) {
                response.body()?.results ?: emptyList()
            } else {
                throw HttpException(response)
            }
        } catch (e: IOException) {
            throw IOException("Error de conexión: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Error al obtener Pokémon: ${e.message}")
        }
    }

    suspend fun getPokemonDetail(name: String): PokemonDetail {
        return try {
            val response = apiService.getPokemonDetail(name)
            if (response.isSuccessful) {
                response.body() ?: throw Exception("Respuesta vacía")
            } else {
                throw HttpException(response)
            }
        } catch (e: IOException) {
            throw IOException("Error de conexión: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Error al obtener detalle: ${e.message}")
        }
    }
}