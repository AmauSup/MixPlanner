package com.supdevinci.mixplanner.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.supdevinci.mixplanner.viewmodel.RandomCocktailState
import com.supdevinci.mixplanner.viewmodel.RandomCocktailViewModel


@Composable
fun HomeScreen(
    viewModel: RandomCocktailViewModel = viewModel(),
    onGoToSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "MixPlanner - Accueil",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val currentState = state.value) {
                RandomCocktailState.Idle,
                RandomCocktailState.Loading -> {
                    CircularProgressIndicator()
                }

                is RandomCocktailState.Success -> {
                    Text(
                        text = currentState.drink.strDrink ?: "Cocktail inconnu",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    Text(
                        text = currentState.drink.strCategory ?: "",
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = currentState.drink.strAlcoholic ?: "",
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = currentState.drink.strGlass ?: "",
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Button(
                        onClick = { viewModel.getRandomCocktail() },
                        modifier = Modifier.padding(top = 24.dp)
                    ) {
                        Text("Découvrir un autre cocktail")
                    }
                }

                is RandomCocktailState.Error -> {
                    Text(
                        text = currentState.message,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = { viewModel.getRandomCocktail() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Réessayer")
                    }
                }
            }
        }
    }
}
