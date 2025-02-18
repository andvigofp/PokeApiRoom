package com.example.pokeapipokedexroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.pokeapipokedexroom.ui.navigation.PokeApp
import com.example.pokeapipokedexroom.ui.screens.PokemonList
import com.example.pokeapipokedexroom.ui.state.PokemonListViewModel
import com.example.pokeapipokedexroom.ui.theme.PokeApiPokedexRoomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokeApiPokedexRoomTheme {
               PokeApp()
            }
        }
    }
}





@Preview(showSystemUi = true)
@Composable
fun PokemonListDefaultPreview() {
    val navController = rememberNavController()
    val viewModel: PokemonListViewModel = viewModel()
    PokeApiPokedexRoomTheme {
        PokemonList(navController, viewModel)
    }
}
