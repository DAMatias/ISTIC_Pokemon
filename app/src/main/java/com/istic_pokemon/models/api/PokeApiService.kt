package com.istic_pokemon.models.api

import com.istic_pokemon.models.PokemonDetail
import com.istic_pokemon.models.PokemonResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {

    @GET("pokemon?limit=150")
    suspend fun getPokemons(): Response<PokemonResponse>

    @GET("pokemon/{name}")
    suspend fun getPokemonDetail(@Path("name") pokemonName: String): Response<PokemonDetail>
}