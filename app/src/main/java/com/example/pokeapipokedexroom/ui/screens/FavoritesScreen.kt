package com.example.pokeapipokedexroom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pokeapipokedexroom.ui.state.PokemonListViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import com.example.pokeapipokedexroom.entidades.PokemonEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavController) {
    val viewModel: PokemonListViewModel = viewModel()
    val pokemonList by viewModel.favoritePokemonList.collectAsStateWithLifecycle()
    var pokemonToDelete by remember { mutableStateOf<PokemonEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadFavorites()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Mis Pokémon Favoritos") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver a HomeScreen")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("add_pokemon") }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Añadir Pokémon")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = viewModel.searchText,
                onValueChange = { viewModel.searchText = it },
                placeholder = { Text("Buscar Pokémon") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.onSurface
                )
            )
            // Utilizando `pokemonList` para filtrar y mostrar la lista
            val filteredPokemonList = if (viewModel.searchText.isEmpty()) {
                pokemonList
            } else {
                pokemonList.filter { it.name.contains(viewModel.searchText, ignoreCase = true) }
            }

            if (filteredPokemonList.isEmpty()) {
                Text(
                    text = "No hay Pokémon favoritos. Añade algunos usando el botón \"+\".",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // Dos columnas
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredPokemonList.size) { index ->
                        val pokemon = filteredPokemonList[index]
                        val gradientColor = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF87CEFA),  // Color más oscuro en la parte superior
                                Color(0xFFB0E0E6)   // Color más claro en la parte inferior
                            )
                        )

                        val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${pokemon.id}.png"

                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable { /* No hacer nada al hacer clic en la carta */ },
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.Transparent
                            )
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(gradientColor)
                                    .padding(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(gradientColor)
                                ) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = "Imagen de ${pokemon.name}",
                                        modifier = Modifier
                                            .size(120.dp)
                                            .padding(8.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333),
                                    textAlign = TextAlign.Center
                                )
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                                ) {
                                    IconButton(onClick = { navController.navigate("pokemon_detail/${pokemon.id}") }) {
                                        Icon(Icons.Default.Visibility, contentDescription = "Ver detalles")
                                    }
                                    IconButton(onClick = { pokemonToDelete = pokemon }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar Pokémon")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar un Pokémon
    pokemonToDelete?.let { pokemon ->
        AlertDialog(
            onDismissRequest = { pokemonToDelete = null },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar a '${pokemon.name}' de tus favoritos?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePokemonById(pokemon.id)
                        pokemonToDelete = null
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(onClick = { pokemonToDelete = null }) {
                    Text("No")
                }
            }
        )
    }
}