package com.example.pokeapipokedexroom.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pokeapipokedexroom.data.Pokemon
import com.google.gson.Gson

@Entity(tableName = "pokemon")
data class PokemonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val url: String,
    val height: Int,
    val weight: Int,
    val description: String,
    val types: String, // Almacenar como cadena JSON
    val stats: String  // Almacenar como cadena JSON
) {
    val imageUrl: String
        get() = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}
