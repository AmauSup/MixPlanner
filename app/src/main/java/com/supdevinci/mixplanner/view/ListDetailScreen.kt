package com.supdevinci.mixplanner.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.supdevinci.mixplanner.data.local.model.CocktailListWithCocktails

@Composable
fun ListDetailScreen(
    list: CocktailListWithCocktails,
    onBack: () -> Unit,
    onCocktailClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Retour"
            )
        }

        Text(
            text = list.list.name,
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "${list.cocktails.size} cocktail(s)",
            modifier = Modifier.padding(top = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            items(list.cocktails, key = { it.id }) { cocktail ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onCocktailClick(cocktail.remoteId)
                        }
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = cocktail.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (!cocktail.category.isNullOrBlank()) {
                        Text("Catégorie : ${cocktail.category}")
                    }

                    if (!cocktail.alcoholic.isNullOrBlank()) {
                        Text("Type : ${cocktail.alcoholic}")
                    }

                    if (!cocktail.glass.isNullOrBlank()) {
                        Text("Verre : ${cocktail.glass}")
                    }
                }
            }
        }
    }
}