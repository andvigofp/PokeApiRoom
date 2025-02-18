package com.example.pokeapipokedexroom.data

import com.example.pokeapipokedexroom.entidades.PokemonEntity

import com.google.gson.Gson

data class PokemonList(val results: List<Pokemon>)

data class Pokemon(
    val name: String,
    val url: String,
    var height: Int = 0,
    var weight: Int = 0,
    var description: String = "",
    var evolvesFrom: Evolution? = null,
    var evolutions: List<Evolution> = listOf(),
    var types: List<PokemonType> = listOf(),
    var stats: List<PokemonStat> = listOf()
) {
    val id: Int
        get() {
            val components = url.split("/")
            return components[components.size - 2].toIntOrNull() ?: 0
        }

    val imageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}



// Función de extensión para convertir Pokemon a PokemonEntity
fun Pokemon.toEntity(): PokemonEntity {
    return PokemonEntity(
        id = this.id,
        name = this.name,
        url = this.url,
        height = this.height,
        weight = this.weight,
        description = this.description ?: "",  // Proporcionar un valor por defecto
        types = Gson().toJson(this.types),
        stats = Gson().toJson(this.stats)
    )
}
