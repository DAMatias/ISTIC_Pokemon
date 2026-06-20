package com.istic_pokemon.models.api

import com.istic_pokemon.models.PokemonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {

    @GET("pokemon?limit=150")
    suspend fun getPokemons(): Response<PokemonResponse>

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(@Path("name") pokemonName: String): Response<Any>
    // Nota: Puse 'Any' temporalmente. Luego deberás crear un modelo 'PokemonDetail'
    // igual que hicimos en el paso 3, pero con los stats específicos, e intercambiarlo aquí.
}