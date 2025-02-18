package com.example.pokeapipokedexroom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pokeapipokedexroom.ui.screens.AddPokemonScreen
import com.example.pokeapipokedexroom.ui.screens.FavoritesScreen
import com.example.pokeapipokedexroom.ui.screens.HomeScreen
import com.example.pokeapipokedexroom.ui.screens.PokemonDetailScreen
import com.example.pokeapipokedexroom.ui.screens.PokemonList
import com.example.pokeapipokedexroom.ui.state.PokemonListViewModel

@Composable
fun PokeApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("pokemon_list") {
            val viewModel: PokemonListViewModel = viewModel()
            PokemonList(navController, viewModel)
        }
        composable("pokemon_detail/{pokemonId}") { backStackEntry ->
            val viewModel: PokemonListViewModel = viewModel()
            val pokemonId = backStackEntry.arguments?.getString("pokemonId")?.toInt() ?: 0
            PokemonDetailScreen(viewModel, pokemonId, navController)
        }
        composable("favorites") {
            FavoritesScreen(navController)
        }
        composable("add_pokemon") {
            AddPokemonScreen(navController)
        }
    }
}