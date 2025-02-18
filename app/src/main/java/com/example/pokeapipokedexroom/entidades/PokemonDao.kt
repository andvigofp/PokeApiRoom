package com.example.pokeapipokedexroom.entidades

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pokemons: List<PokemonEntity>)

    @Query("SELECT * FROM pokemon")
    suspend fun getAllPokemon(): List<PokemonEntity>

    @Query("DELETE FROM pokemon WHERE id = :id")
    suspend fun deletePokemonById(id: Int)

    @Query("DELETE FROM pokemon")
    suspend fun deleteAllPokemons()
}
