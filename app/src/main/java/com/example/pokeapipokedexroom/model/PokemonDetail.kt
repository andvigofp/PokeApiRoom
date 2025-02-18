package com.example.pokeapipokedexroom.model

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pokeapipokedexroom.data.Pokemon

@Composable
fun PokemonDetail(pokemon: Pokemon, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),  // Añadir espacio inferior
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Imagen del Pokémon
                    AsyncImage(
                        model = pokemon.imageUrl,
                        contentDescription = "Imagen del Pokémon",
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Información general del Pokémon
                    Text(
                        text = "#${pokemon.id} ${pokemon.name.replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Altura",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "${pokemon.height / 10.0} m")
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Peso",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "${pokemon.weight / 10.0} kg")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Descripción:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()  // Asegurar ancho completo
                    )
                    Text(
                        text = pokemon.description,
                        modifier = Modifier.fillMaxWidth()  // Asegurar ancho completo
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Añadir los tipos del Pokémon con colores
                    Text(
                        text = "Tipo(s):",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        pokemon.types.forEach { type ->
                            val (typeColor, typeName) = when (type.name.lowercase()) {
                                "grass" -> Color(0xFF78C850) to "Planta"
                                "fire" -> Color(0xFFF08030) to "Fuego"
                                "water" -> Color(0xFF6890F0) to "Agua"
                                "bug" -> Color(0xFFA8B820) to "Bicho"
                                "normal" -> Color(0xFFA8A878) to "Normal"
                                "poison" -> Color(0xFFA040A0) to "Veneno"
                                "electric" -> Color(0xFFF8D030) to "Eléctrico"
                                "ground" -> Color(0xFFE0C068) to "Tierra"
                                "fairy" -> Color(0xFFEE99AC) to "Hada"
                                "fighting" -> Color(0xFFC03028) to "Lucha"
                                "psychic" -> Color(0xFFF85888) to "Psíquico"
                                "rock" -> Color(0xFFB8A038) to "Roca"
                                "ghost" -> Color(0xFF705898) to "Fantasma"
                                "ice" -> Color(0xFF98D8D8) to "Hielo"
                                "dragon" -> Color(0xFF7038F8) to "Dragón"
                                "dark" -> Color(0xFF705848) to "Siniestro"
                                "steel" -> Color(0xFFB8B8D0) to "Acero"
                                "flying" -> Color(0xFFA890F0) to "Volador"
                                else -> Color.Gray to type.name.replaceFirstChar { it.uppercase() }
                            }
                            Text(
                                text = typeName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .background(typeColor, shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Añadir las estadísticas base del Pokémon con colores y textos dentro de las barras
                    Text(
                        text = "Estadísticas Base:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column {
                        pokemon.stats.forEach { stat ->
                            val (statName, statColor) = when (stat.name.lowercase()) {
                                "hp" -> "PS" to Color(0xFFF5C84C)
                                "attack" -> "Ataque" to Color(0xFFFB6C6C)
                                "defense" -> "Defensa" to Color(0xFF77BDFE)
                                "special-attack" -> "Ataque Especial" to Color(0xFF9A78FA)
                                "special-defense" -> "Defensa Especial" to Color(0xFFFA92B2)
                                "speed" -> "Velocidad" to Color(0xFF4ADF86)
                                else -> stat.name.replaceFirstChar { it.uppercase() } to Color.Gray
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(24.dp)
                                        .background(statColor, shape = RoundedCornerShape(8.dp))
                                ) {
                                    Text(
                                        text = "$statName: ${stat.value}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    if (pokemon.evolutions.isNotEmpty()) {
                        Text(
                            text = "Evoluciones:",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        pokemon.evolutions.forEach { evolution ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = evolution.imageUrl,
                                    contentDescription = "Imagen de la Evolución",
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                                        .padding(8.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = evolution.name.replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                if (evolution.method != "Desconocido") {
                                    Text(
                                        text = "Método: ${evolution.method}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Light,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "Este Pokémon no evoluciona.",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
