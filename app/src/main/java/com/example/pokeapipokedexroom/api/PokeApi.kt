package com.example.pokeapipokedexroom.api

import com.example.pokeapipokedexroom.data.ChainLink
import com.example.pokeapipokedexroom.data.EvolutionChain
import com.example.pokeapipokedexroom.data.EvolutionDetail
import com.example.pokeapipokedexroom.data.NamedAPIResource
import com.example.pokeapipokedexroom.data.Pokemon
import com.example.pokeapipokedexroom.data.PokemonDetails
import com.example.pokeapipokedexroom.data.PokemonList
import com.example.pokeapipokedexroom.data.PokemonSpecies
import com.example.pokeapipokedexroom.data.PokemonStat
import com.example.pokeapipokedexroom.data.PokemonType
import com.example.pokeapipokedexroom.data.Sprites
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object PokemonAPI {
    interface PokemonService {
        @GET("pokemon?limit=1025")
        fun loadPokemon(): Call<PokemonList>

        @GET("pokemon/{id}")
        fun getPokemonDetails(@Path("id") id: Int): Call<PokemonDetails>

        @GET("pokemon-species/{id}")
        fun getPokemonSpecies(@Path("id") id: Int): Call<PokemonSpecies>

        @GET("evolution-chain/{id}")
        fun getEvolutionChain(@Path("id") id: Int): Call<EvolutionChain>
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(PokemonService::class.java)

    fun loadPokemon(success: (pokemonList: List<Pokemon>) -> Unit, failure: () -> Unit) {
        service.loadPokemon().enqueue(object : Callback<PokemonList> {
            override fun onResponse(call: Call<PokemonList>, response: Response<PokemonList>) {
                success(response.body()?.results ?: listOf())
            }

            override fun onFailure(call: Call<PokemonList>, t: Throwable) {
                failure()
            }
        })
    }

    fun getPokemonDetails(
        id: Int,
        success: (pokemonDetails: PokemonDetails) -> Unit,
        failure: () -> Unit
    ) {
        service.getPokemonDetails(id).enqueue(object : Callback<PokemonDetails> {
            override fun onResponse(
                call: Call<PokemonDetails>,
                response: Response<PokemonDetails>
            ) {
                if (response.isSuccessful) {
                    success(
                        response.body() ?: PokemonDetails(
                            id = id,  // Pasar el ID correcto
                            name = "",
                            sprites = Sprites(),
                            height = 0,
                            weight = 0,
                            types = listOf(),
                            stats = listOf()
                        )
                    )
                } else {
                    failure()
                }
            }

            override fun onFailure(call: Call<PokemonDetails>, t: Throwable) {
                failure()
            }
        })
    }

    fun getPokemonSpecies(
        id: Int,
        success: (pokemonSpecies: PokemonSpecies) -> Unit,
        failure: () -> Unit
    ) {
        service.getPokemonSpecies(id).enqueue(object : Callback<PokemonSpecies> {
            override fun onResponse(
                call: Call<PokemonSpecies>,
                response: Response<PokemonSpecies>
            ) {
                if (response.isSuccessful) {
                    val species = response.body() ?: PokemonSpecies(
                        flavor_text_entries = emptyList(),
                        evolves_from_species = null,
                        evolution_chain = NamedAPIResource("", "")
                    )
                    success(species)
                } else {
                    failure()
                }
            }

            override fun onFailure(call: Call<PokemonSpecies>, t: Throwable) {
                failure()
            }
        })
    }

    fun getEvolutionChain(
        id: Int,
        success: (evolutionChain: EvolutionChain) -> Unit,
        failure: () -> Unit
    ) {
        service.getEvolutionChain(id).enqueue(object : Callback<EvolutionChain> {
            override fun onResponse(
                call: Call<EvolutionChain>,
                response: Response<EvolutionChain>
            ) {
                if (response.isSuccessful) {
                    success(
                        response.body() ?: EvolutionChain(
                            ChainLink(
                                NamedAPIResource("", ""),
                                evolves_to = listOf(),
                                evolution_details = listOf()
                            )
                        )
                    )
                } else {
                    failure()
                }
            }

            override fun onFailure(call: Call<EvolutionChain>, t: Throwable) {
                failure()
            }
        })
    }

    suspend fun getPokemonById(id: Int): PokemonDetails? {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.getPokemonDetails(id).execute()
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    fun getEvolutionMethod(detail: EvolutionDetail): String {
        return when {
            detail.min_level != null -> "Nivel ${detail.min_level}"
            detail.item != null -> "Piedra: ${detail.item.name}"
            detail.min_happiness != null -> "Felicidad"
            detail.held_item != null -> "Objeto Equipado: ${detail.held_item.name}"
            detail.trade_species != null -> "Intercambio"
            detail.trigger != null -> when (detail.trigger.name) {
                "level-up" -> "Nivel ${detail.min_level ?: "Desconocido"}"
                "trade" -> "Intercambio"
                "use-item" -> "Usar ${detail.item?.name ?: "Desconocido"}"
                else -> "Desconocido"
            }
            else -> "Desconocido"
        }
    }

    fun parsePokemonDetails(details: PokemonDetails, species: PokemonSpecies): Pokemon {
        val url = "https://pokeapi.co/api/v2/pokemon/${details.id}/"  // Crear la URL basada en el ID
        return Pokemon(
            name = details.name,
            url = url,  // Pasar la URL correcta
            height = details.height,
            weight = details.weight,
            description = species.flavor_text_entries.firstOrNull { it.language.name == "es" }?.flavor_text
                ?: "Descripción no disponible",
            types = details.types.map { PokemonType(it.type.name) },
            stats = details.stats.map { PokemonStat(it.stat.name, it.base_stat) }
        )
    }
}
