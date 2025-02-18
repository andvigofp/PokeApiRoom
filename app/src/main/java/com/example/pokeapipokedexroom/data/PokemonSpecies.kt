package com.example.pokeapipokedexroom.data


data class PokemonSpecies(
    val flavor_text_entries: List<FlavorTextEntry>,
    val evolves_from_species: NamedAPIResource?,
    val evolution_chain: NamedAPIResource
)

data class FlavorTextEntry(
    val flavor_text: String,
    val language: Language
)

data class Language(
    val name: String
)
