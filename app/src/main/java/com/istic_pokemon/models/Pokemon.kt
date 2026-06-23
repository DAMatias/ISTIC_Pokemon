package com.istic_pokemon.models

data class Pokemon(
    val name: String,
    val url: String,
    var typesStr: String = ""
)