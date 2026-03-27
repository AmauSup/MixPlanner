package com.supdevinci.mixplanner.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supdevinci.mixplanner.model.Drink
import com.supdevinci.mixplanner.viewmodel.SearchState
import com.supdevinci.mixplanner.viewmodel.SearchViewModel

private enum class SearchMode {
    NAME,
    INGREDIENT
}

@Composable
private fun CocktailSearchItem(
    drink: Drink,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable { onClick() }
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = drink.strDrink ?: "Cocktail inconnu",
                style = MaterialTheme.typography.titleMedium
            )

            CocktailImage(
                imageUrl = drink.strDrinkThumb,
                contentDescription = drink.strDrink,
                modifier = Modifier.padding(top = 8.dp),
                height = 180
            )

            if (!drink.strCategory.isNullOrBlank()) {
                Text(
                    text = drink.strCategory,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (!drink.strAlcoholic.isNullOrBlank()) {
                Text(
                    text = drink.strAlcoholic,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (!drink.strGlass.isNullOrBlank()) {
                Text(
                    text = drink.strGlass,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterDropdown(
    label: String,
    selectedValue: String?,
    options: List<String>,
    onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedValue ?: "Tous",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Tous") },
                onClick = {
                    onSelected(null)
                    expanded = false
                }
            )

            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.replace('_', ' ')) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onGoToHome: () -> Unit,
    onOpenDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }
    var searchMode by remember { mutableStateOf(SearchMode.NAME) }
    var filtersVisible by remember { mutableStateOf(false) }

    val state = viewModel.state.collectAsStateWithLifecycle()
    val alcoholicOptions = viewModel.alcoholicOptions.collectAsStateWithLifecycle()
    val categoryOptions = viewModel.categoryOptions.collectAsStateWithLifecycle()
    val glassOptions = viewModel.glassOptions.collectAsStateWithLifecycle()
    val suggestions = viewModel.suggestions.collectAsStateWithLifecycle()

    var selectedAlcoholic by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedGlass by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Recherche",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            item {
                Text(
                    text = "Mode de recherche",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(onClick = {
                        searchMode = SearchMode.NAME
                        viewModel.clearSuggestions()
                    }) {
                        Text("Par nom")
                    }

                    OutlinedButton(onClick = {
                        searchMode = SearchMode.INGREDIENT
                        viewModel.clearSuggestions()
                    }) {
                        Text("Par ingrédient")
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        viewModel.updateSuggestions(
                            query = it,
                            byIngredient = (searchMode == SearchMode.INGREDIENT)
                        )
                    },
                    label = {
                        Text(
                            if (searchMode == SearchMode.NAME) "Nom du cocktail"
                            else "Nom de l'ingrédient"
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (suggestions.value.isNotEmpty()) {
                items(suggestions.value) { suggestion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                query = suggestion
                                viewModel.clearSuggestions()
                                focusManager.clearFocus()
                            }
                    ) {
                        Text(
                            text = suggestion,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = { filtersVisible = !filtersVisible }
                ) {
                    Text(
                        if (filtersVisible) "Masquer les filtres"
                        else "Afficher les filtres"
                    )
                }
            }

            if (filtersVisible) {
                item {
                    FilterDropdown(
                        label = "Alcool",
                        selectedValue = selectedAlcoholic?.replace('_', ' '),
                        options = alcoholicOptions.value,
                        onSelected = {
                            selectedAlcoholic = it
                            viewModel.setAlcoholicFilter(it)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    FilterDropdown(
                        label = "Catégorie",
                        selectedValue = selectedCategory?.replace('_', ' '),
                        options = categoryOptions.value,
                        onSelected = {
                            selectedCategory = it
                            viewModel.setCategoryFilter(it)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    FilterDropdown(
                        label = "Verre",
                        selectedValue = selectedGlass?.replace('_', ' '),
                        options = glassOptions.value,
                        onSelected = {
                            selectedGlass = it
                            viewModel.setGlassFilter(it)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.runSearch(
                                query = query,
                                byIngredient = (searchMode == SearchMode.INGREDIENT)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Rechercher")
                    }

                    if (filtersVisible) {
                        OutlinedButton(
                            onClick = {
                                selectedAlcoholic = null
                                selectedCategory = null
                                selectedGlass = null
                                viewModel.clearAllFilters()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Réinitialiser")
                        }
                    }
                }
            }

            when (val currentState = state.value) {
                SearchState.Idle -> {
                    item {
                        Text("Tu peux lancer une recherche, ou laisser vide pour parcourir les cocktails et filtrer.")
                    }
                }

                SearchState.Loading -> {
                    item {
                        CircularProgressIndicator()
                    }
                }

                is SearchState.Success -> {
                    item {
                        Text(
                            text = "Résultats : ${currentState.filteredDrinks.size}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    items(
                        currentState.filteredDrinks,
                        key = { it.idDrink ?: it.strDrink.orEmpty() }
                    ) { drink ->
                        CocktailSearchItem(
                            drink = drink,
                            onClick = {
                                val id = drink.idDrink
                                if (!id.isNullOrBlank()) {
                                    onOpenDetails(id)
                                }
                            }
                        )
                    }
                }

                is SearchState.Error -> {
                    item {
                        Text(
                            text = currentState.message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}