package com.example.pokeapipokedexroom.ui.state

import android.app.Application
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapipokedexroom.api.PokemonAPI
import com.example.pokeapipokedexroom.data.ChainLink
import com.example.pokeapipokedexroom.data.Evolution
import com.example.pokeapipokedexroom.data.EvolutionChain
import com.example.pokeapipokedexroom.data.EvolutionDetail
import com.example.pokeapipokedexroom.data.Pokemon
import com.example.pokeapipokedexroom.data.PokemonDetails
import com.example.pokeapipokedexroom.data.PokemonSpecies
import com.example.pokeapipokedexroom.data.toEntity
import com.example.pokeapipokedexroom.entidades.PokemonDatabase
import com.example.pokeapipokedexroom.entidades.PokemonEntity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PokemonListViewModel(application: Application) : AndroidViewModel(application) {
    private val pokemonDao = PokemonDatabase.getDatabase(application).pokemonDao()
    var pokemonList: List<Pokemon> by mutableStateOf(listOf())
    var searchText = MutableStateFlow("")
    var selectedPokemon = MutableStateFlow<Pokemon?>(null)
    var error = MutableStateFlow(false)
    var pokemonDetails = MutableStateFlow<Pokemon?>(null)
    var detailsError = MutableStateFlow(false)
    var pokemonToDelete = MutableStateFlow<PokemonEntity?>(null)
    var showSuccessDialog = MutableStateFlow(false)
    var showErrorDialog = MutableStateFlow(false)
    var showExistsDialog = MutableStateFlow(false)
    var showLoadingDialog = MutableStateFlow(false)
    var expanded = MutableStateFlow(false)
    private val _favoritePokemonList = MutableStateFlow<List<PokemonEntity>>(emptyList())
    val favoritePokemonList: StateFlow<List<PokemonEntity>> = _favoritePokemonList

    val filteredPokemonList: List<Pokemon>
        get() = if (searchText.value.isEmpty()) {
            pokemonList
        } else {
            pokemonList.filter { it.name.contains(searchText.value, ignoreCase = true) }
        }

    val filteredFavoritePokemonList: List<PokemonEntity>
        get() = if (searchText.value.isEmpty()) {
            _favoritePokemonList.value
        } else {
            _favoritePokemonList.value.filter { it.name.contains(searchText.value, ignoreCase = true) }
        }

    init {
        loadData()
        loadFavorites()
    }

    fun loadData() {
        PokemonAPI.loadPokemon({ pokemon ->
            pokemonList = pokemon
        }, {
            println("Error al cargar la lista de Pokémon")
        })
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _favoritePokemonList.value = pokemonDao.getAllPokemon()
        }
    }

    fun addFavoritePokemonByNumber(number: Int) {
        val existingPokemon = _favoritePokemonList.value.any { it.id == number }
        if (existingPokemon) {
            showExistsDialog.value = true
            return
        }

        showLoadingDialog.value = true
        viewModelScope.launch {
            val exists = checkPokemonExists(number)
            showLoadingDialog.value = false
            if (exists) {
                pokemonList.find { it.id == number }?.let { pokemon ->
                    val entity = pokemon.toEntity()
                    pokemonDao.insertPokemon(entity)
                    loadFavorites()
                    showSuccessDialog.value = true
                }
            } else {
                showErrorDialog.value = true
            }
        }
    }

    suspend fun checkPokemonExists(number: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = PokemonAPI.getPokemonById(number)
                response != null
            } catch (e: Exception) {
                false
            }
        }
    }

    fun deletePokemonById(id: Int) {
        viewModelScope.launch {
            pokemonDao.deletePokemonById(id)
            loadFavorites()
        }
    }

    fun deleteAllPokemons() {
        viewModelScope.launch {
            pokemonDao.deleteAllPokemons()
            loadFavorites()
        }
    }

    fun loadPokemonDetails(id: Int, success: (pokemon: Pokemon) -> Unit, failure: () -> Unit) {
        val cachedPokemon = pokemonList.find { it.id == id }
        if (cachedPokemon != null) {
            success(cachedPokemon)
        } else {
            PokemonAPI.getPokemonDetails(id, { details ->
                PokemonAPI.getPokemonSpecies(id, { species ->
                    val parsedPokemon = PokemonAPI.parsePokemonDetails(details, species)

                    val evolvesFrom = species.evolves_from_species?.let {
                        Evolution(
                            it.name,
                            null,
                            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${extractPokemonId(it.url)}.png"
                        )
                    }

                    val evolutionChainId = extractEvolutionChainId(species.evolution_chain.url)
                    PokemonAPI.getEvolutionChain(evolutionChainId, { chain ->
                        val evolutions = parseEvolutionChain(chain.chain)

                        parsedPokemon.evolutions = evolutions
                        parsedPokemon.evolvesFrom = evolvesFrom
                        success(parsedPokemon)
                    }, {
                        failure()
                    })
                }, {
                    failure()
                })
            }, {
                failure()
            })
        }
    }

    fun loadPokemonDetails(id: Int) {
        viewModelScope.launch {
            val cachedPokemon = pokemonList.find { it.id == id }
            if (cachedPokemon != null) {
                pokemonDetails.value = cachedPokemon
                detailsError.value = false
            } else {
                PokemonAPI.getPokemonDetails(id, { details ->
                    PokemonAPI.getPokemonSpecies(id, { species ->
                        val parsedPokemon = PokemonAPI.parsePokemonDetails(details, species)

                        val evolvesFrom = species.evolves_from_species?.let {
                            Evolution(
                                it.name,
                                null,
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${extractPokemonId(it.url)}.png"
                            )
                        }

                        val evolutionChainId = extractEvolutionChainId(species.evolution_chain.url)
                        PokemonAPI.getEvolutionChain(evolutionChainId, { chain ->
                            val evolutions = parseEvolutionChain(chain.chain)

                            parsedPokemon.evolutions = evolutions
                            parsedPokemon.evolvesFrom = evolvesFrom
                            pokemonDetails.value = parsedPokemon
                            detailsError.value = false
                        }, {
                            detailsError.value = true
                        })
                    }, {
                        detailsError.value = true
                    })
                }, {
                    detailsError.value = true
                })
            }
        }
    }

    fun setExpandedState(expanded: Boolean) {
        this.expanded.value = expanded
    }

    fun setSearchText(text: String) {
        searchText.value = text
    }

    fun setPokemonToDeleteState(pokemon: PokemonEntity?) {
        pokemonToDelete.value = pokemon
    }

    fun setSelectedPokemon(pokemon: Pokemon?) {
        selectedPokemon.value = pokemon
    }

    fun setShowSuccessDialog(show: Boolean) {
        showSuccessDialog.value = show
    }

    fun setShowErrorDialog(show: Boolean) {
        showErrorDialog.value = show
    }

    fun setShowExistsDialog(show: Boolean) {
        showExistsDialog.value = show
    }

    fun setShowLoadingDialog(show: Boolean) {
        showLoadingDialog.value = show
    }

    fun setError(show: Boolean) {
        error.value = show
    }

    private fun extractPokemonId(url: String): Int {
        val components = url.split("/")
        return components[components.size - 2].toIntOrNull() ?: 0
    }

    private fun extractEvolutionChainId(url: String): Int {
        val components = url.split("/")
        return components[components.size - 2].toIntOrNull() ?: 0
    }

    private fun parseEvolutionChain(chain: ChainLink): List<Evolution> {
        val evolutions = mutableListOf<Evolution>()

        fun addEvolutions(chainLink: ChainLink) {
            chainLink.evolves_to.forEach { link ->
                link.evolution_details.forEach { detail ->
                    val method = PokemonAPI.getEvolutionMethod(detail)
                    val evolution = Evolution(
                        name = link.species.name,
                        method = method,
                        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${extractPokemonId(link.species.url)}.png"
                    )
                    evolutions.add(evolution)
                }
                addEvolutions(link)
            }
        }

        addEvolutions(chain)
        return evolutions
    }
}
