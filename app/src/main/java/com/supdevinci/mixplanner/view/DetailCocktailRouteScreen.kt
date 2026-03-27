package com.supdevinci.mixplanner.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.supdevinci.mixplanner.viewmodel.CocktailListsViewModel
import com.supdevinci.mixplanner.viewmodel.DetailCocktailState
import com.supdevinci.mixplanner.viewmodel.DetailCocktailViewModel

@Composable
fun DetailCocktailRouteScreen(
    cocktailId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailCocktailViewModel = viewModel(),
    listsViewModel: CocktailListsViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lists by listsViewModel.visibleLists.collectAsStateWithLifecycle()

    LaunchedEffect(cocktailId) {
        viewModel.loadCocktail(cocktailId)
    }

    when (val currentState = state) {
        DetailCocktailState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is DetailCocktailState.Success -> {
            CocktailDetailScreen(
                drink = currentState.drink,
                lists = lists,
                onBackToSearch = onBack,
                onAddToLists = { selectedIds ->
                    listsViewModel.addDrinkToLists(selectedIds, currentState.drink)
                },
                onCreateList = { name ->
                    listsViewModel.createList(name)
                },
                modifier = modifier
            )
        }

        is DetailCocktailState.Error -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}