package com.example.pokeapipokedexroom.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pokeapipokedexroom.ui.state.PokemonListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPokemonScreen(navController: NavController) {
    val viewModel: PokemonListViewModel = viewModel()
    var pokemonNumber by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showExistsDialog by remember { mutableStateOf(false) }
    var showLoadingDialog by remember { mutableStateOf(false) }

    fun addPokemon() {
        val number = pokemonNumber.toIntOrNull()
        if (number == null || number < 1 || number > 1025) {
            showErrorDialog = true
        } else {
            viewModel.viewModelScope.launch {
                // Verificar si el Pokémon ya está en favoritos
                val existingPokemon = viewModel.favoritePokemonList.value.any { it.id == number }
                if (existingPokemon) {
                    showExistsDialog = true
                } else {
                    showLoadingDialog = true
                    val exists = viewModel.checkPokemonExists(number)
                    showLoadingDialog = false
                    if (exists) {
                        viewModel.addFavoritePokemonByNumber(number)
                        showSuccessDialog = true
                    } else {
                        showErrorDialog = true
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Añadir Pokémon") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Regresar a Home")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = pokemonNumber,
                onValueChange = { pokemonNumber = it.filter { char -> char.isDigit() } },
                label = { Text("Número del Pokémon") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Button(
                    onClick = {
                        addPokemon()
                    },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Añadir Pokémon")
                }
                Button(
                    onClick = { navController.navigate("favorites") },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }

    // Diálogo de alerta si el Pokémon no existe
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Pokémon no existe") },
            text = { Text("El número de Pokémon no existe en la Pokédex. Por favor, introduce un número entre 1 y 1025.") },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Diálogo de alerta si el Pokémon ya existe
    if (showExistsDialog) {
        AlertDialog(
            onDismissRequest = { showExistsDialog = false },
            title = { Text("Pokémon ya existe") },
            text = { Text("Ya existe un Pokémon con ese número en tus favoritos. Por favor, introduce otro número.") },
            confirmButton = {
                TextButton(onClick = { showExistsDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    // Diálogo de alerta si el Pokémon se ha añadido correctamente
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Pokémon añadido") },
            text = { Text("El Pokémon se ha añadido correctamente a tus favoritos.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    navController.navigate("favorites") // Navegar a la pantalla de favoritos
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Diálogo de carga mientras se verifica la existencia del Pokémon
    if (showLoadingDialog) {
        AlertDialog(
            onDismissRequest = { showLoadingDialog = false },
            title = { Text("Verificando") },
            text = { Text("Por favor espera mientras verificamos la existencia del Pokémon...") },
            confirmButton = { }
        )
    }
}