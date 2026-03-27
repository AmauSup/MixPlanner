package com.supdevinci.mixplanner.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.supdevinci.mixplanner.data.local.model.CocktailListWithCocktails
import com.supdevinci.mixplanner.viewmodel.CocktailListsState
import com.supdevinci.mixplanner.viewmodel.CocktailListsViewModel

@Composable
fun ListsScreen(
    viewModel: CocktailListsViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    var newListName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mes listes",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newListName,
            onValueChange = { newListName = it },
            label = { Text("Nom de la nouvelle liste") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.createList(newListName)
                newListName = ""
            }
        ) {
            Text("Créer une liste")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val currentState = state.value) {
            CocktailListsState.Loading -> CircularProgressIndicator()

            CocktailListsState.Empty -> Text("Aucune liste créée.")

            is CocktailListsState.Success -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(currentState.lists, key = { it.list.id }) { item ->
                        ListCard(
                            list = item,
                            onRename = { newName ->
                                viewModel.renameList(item.list.id, newName)
                            },
                            onDelete = {
                                viewModel.softDeleteList(item.list.id)
                            }
                        )
                    }
                }
            }

            is CocktailListsState.Error -> {
                Text(
                    text = currentState.message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun ListCard(
    list: CocktailListWithCocktails,
    onRename: (String) -> Unit,
    onDelete: () -> Unit
) {
    var renameValue by remember { mutableStateOf(list.list.name) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Text(
            text = list.list.name,
            style = MaterialTheme.typography.titleMedium
        )

        Text(
            text = "${list.cocktails.size} cocktail(s)",
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = renameValue,
            onValueChange = { renameValue = it },
            label = { Text("Renommer la liste") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onRename(renameValue) }) {
                Text("Renommer")
            }

            Button(onClick = onDelete) {
                Text("Supprimer")
            }
        }
    }
}