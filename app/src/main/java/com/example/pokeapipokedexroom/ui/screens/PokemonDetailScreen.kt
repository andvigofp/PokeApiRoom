package com.example.pokeapipokedexroom.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.pokeapipokedexroom.data.Pokemon
import com.example.pokeapipokedexroom.model.PokemonDetail
import com.example.pokeapipokedexroom.ui.state.PokemonListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(viewModel: PokemonListViewModel, pokemonId: Int, navController: NavController) {
    var pokemon by remember { mutableStateOf<Pokemon?>(null) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(pokemonId) {
        viewModel.loadPokemonDetails(pokemonId, { loadedPokemon ->
            pokemon = loadedPokemon
        }, {
            error = true
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detalles del Pokémon") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when {
            pokemon != null -> PokemonDetail(pokemon!!, Modifier.padding(padding))
            error -> Text(text = "Error al cargar los detalles del Pokémon", modifier = Modifier.padding(padding))
            else -> Text(text = "Cargando detalles del Pokémon...", modifier = Modifier.padding(padding))
        }
    }
}
