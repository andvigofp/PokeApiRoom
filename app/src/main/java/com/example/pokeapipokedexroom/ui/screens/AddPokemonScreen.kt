package com.example.pokeapipokedexroom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.pokeapipokedexroom.data.Pokemon
import com.example.pokeapipokedexroom.ui.state.PokemonListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPokemonScreen(navController: NavController) {
    val viewModel: PokemonListViewModel = viewModel()
    val expanded by viewModel.expanded.collectAsStateWithLifecycle()
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()
    val selectedPokemon by viewModel.selectedPokemon.collectAsStateWithLifecycle()
    val showSuccessDialog by viewModel.showSuccessDialog.collectAsStateWithLifecycle()
    val showErrorDialog by viewModel.showErrorDialog.collectAsStateWithLifecycle()
    val showExistsDialog by viewModel.showExistsDialog.collectAsStateWithLifecycle()
    val showLoadingDialog by viewModel.showLoadingDialog.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    fun addPokemon() {
        selectedPokemon?.let { pokemon ->
            viewModel.viewModelScope.launch {
                val existingPokemon = viewModel.favoritePokemonList.value.any { it.id == pokemon.id }
                if (existingPokemon) {
                    viewModel.setShowExistsDialog(true)
                } else {
                    viewModel.setShowLoadingDialog(true)
                    val exists = viewModel.checkPokemonExists(pokemon.id)
                    viewModel.setShowLoadingDialog(false)
                    if (exists) {
                        viewModel.addFavoritePokemonByNumber(pokemon.id)
                        viewModel.setShowSuccessDialog(true)
                    } else {
                        viewModel.setShowErrorDialog(true)
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
            verticalArrangement = Arrangement.Top
        ) {
            Box {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { viewModel.setSearchText(it) },
                    placeholder = { Text("Buscar Pokémon") },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.setExpandedState(true) }) {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                        }
                    },
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.onSurface
                    )
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { viewModel.setExpandedState(false) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    viewModel.filteredPokemonList.filter { it.name.contains(searchText, ignoreCase = true) }.forEach { pokemon ->
                        DropdownMenuItem(
                            text = { Text(pokemon.name.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                viewModel.setSelectedPokemon(pokemon)
                                viewModel.setSearchText(pokemon.name) // Actualizar el campo de búsqueda con el Pokémon seleccionado
                                viewModel.setExpandedState(false)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Button(
                    onClick = { addPokemon() },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Añadir Pokémon")
                }
                Button(
                    onClick = { navController.navigate("favorites") },
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("Cancelar")
                }
            }
        }
    }

    // Diálogo de error genérico
    if (error) {
        AlertDialog(
            onDismissRequest = { viewModel.setError(false) },
            title = { Text("Error") },
            text = { Text("Ha ocurrido un error. Por favor, intenta nuevamente.") },
            confirmButton = {
                TextButton(onClick = { viewModel.setError(false) }) {
                    Text("OK")
                }
            }
        )
    }

    // Diálogo de alerta si el Pokémon no existe
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowErrorDialog(false) },
            title = { Text("Pokémon no existe") },
            text = { Text("El Pokémon seleccionado no existe en la Pokédex.") },
            confirmButton = {
                TextButton(onClick = { viewModel.setShowErrorDialog(false) }) {
                    Text("OK")
                }
            }
        )
    }

    // Diálogo de alerta si el Pokémon ya existe
    if (showExistsDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowExistsDialog(false) },
            title = { Text("Pokémon ya existe") },
            text = { Text("Ya existe un Pokémon con ese número en tus favoritos. Por favor, selecciona otro Pokémon.") },
            confirmButton = {
                TextButton(onClick = { viewModel.setShowExistsDialog(false) }) {
                    Text("OK")
                }
            }
        )
    }

    // Diálogo de alerta si el Pokémon se ha añadido correctamente
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowSuccessDialog(false) },
            title = { Text("Pokémon añadido") },
            text = { Text("El Pokémon se ha añadido correctamente a tus favoritos.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setShowSuccessDialog(false)
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
            onDismissRequest = { viewModel.setShowLoadingDialog(false) },
            title = { Text("Verificando") },
            text = { Text("Por favor espera mientras verificamos la existencia del Pokémon...") },
            confirmButton = { }
        )
    }
}
