package com.example.pokeapipokedexroom.data

data class PokemonDetails(
    val id: Int,  // Añadir el campo `id`
    val height: Int = 0,
    val weight: Int = 0,
    val description: String = "",
    val evolvesFrom: Evolution? = null,
    val evolutions: List<Evolution> = listOf(),
    val name: String,
    val sprites: Sprites,
    val types: List<Type>,
    val stats: List<Stat>
)

data class Sprites(
    val front_default: String = ""
)

data class Type(
    val type: TypeDetail
)

data class TypeDetail(
    val name: String
)

data class Stat(
    val stat: StatDetail,
    val base_stat: Int
)

data class StatDetail(
    val name: String
)


data class PokemonType(
    val name: String
)

data class PokemonStat(
    val name: String,
    val value: Int
)

data class Evolution(
    val name: String,
    val method: String?,
    val imageUrl: String
)

data class NamedAPIResource(
    val name: String,
    val url: String
)

data class EvolutionChain(
    val chain: ChainLink
)

data class ChainLink(
    val species: NamedAPIResource,
    val evolves_to: List<ChainLink>,
    val evolution_details: List<EvolutionDetail>
)


data class EvolutionDetail(
    val min_level: Int? = null,
    val item: NamedAPIResource? = null,
    val trigger: NamedAPIResource? = null,
    val gender: Int? = null,
    val held_item: NamedAPIResource? = null,
    val known_move: NamedAPIResource? = null,
    val known_move_type: NamedAPIResource? = null,
    val location: NamedAPIResource? = null,
    val min_affection: Int? = null,
    val min_beauty: Int? = null,
    val min_happiness: Int? = null,
    val needs_overworld_rain: Boolean? = null,
    val party_species: NamedAPIResource? = null,
    val party_type: NamedAPIResource? = null,
    val relative_physical_stats: Int? = null,
    val time_of_day: String? = null,
    val trade_species: NamedAPIResource? = null,
    val turn_upside_down: Boolean? = null
)