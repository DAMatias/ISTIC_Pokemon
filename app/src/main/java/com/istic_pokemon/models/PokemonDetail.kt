package com.istic_pokemon.models

data class PokemonDetail(
    val id: Int,
    val name: String,
    val types: List<TypeSlot>,
    val stats: List<StatSlot>,
    val sprites: Sprites
)

data class TypeSlot(
    val slot: Int,
    val type: Type
)

data class Type(
    val name: String
)

data class StatSlot(
    val baseStat: Int,
    val stat: Stat
)

data class Stat(
    val name: String
)

data class Sprites(
    val frontDefault: String?,
    val other: OtherSprites?
)

data class OtherSprites(
    val officialArtwork: OfficialArtwork?
)

data class OfficialArtwork(
    val frontDefault: String?
)