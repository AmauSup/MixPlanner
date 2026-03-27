package com.supdevinci.mixplanner.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.supdevinci.mixplanner.data.local.entities.CocktailListEntity
import com.supdevinci.mixplanner.model.Drink

private fun cocktailIngredients(drink: Drink): List<String> {
    val ingredients = listOf(
        drink.strIngredient1 to drink.strMeasure1,
        drink.strIngredient2 to drink.strMeasure2,
        drink.strIngredient3 to drink.strMeasure3,
        drink.strIngredient4 to drink.strMeasure4,
        drink.strIngredient5 to drink.strMeasure5,
        drink.strIngredient6 to drink.strMeasure6,
        drink.strIngredient7 to drink.strMeasure7,
        drink.strIngredient8 to drink.strMeasure8,
        drink.strIngredient9 to drink.strMeasure9,
        drink.strIngredient10 to drink.strMeasure10,
        drink.strIngredient11 to drink.strMeasure11,
        drink.strIngredient12 to drink.strMeasure12,
        drink.strIngredient13 to drink.strMeasure13,
        drink.strIngredient14 to drink.strMeasure14,
        drink.strIngredient15 to drink.strMeasure15
    )

    return ingredients
        .filter { !it.first.isNullOrBlank() }
        .map { (ingredient, measure) ->
            val prefix = measure?.trim().orEmpty()
            val name = ingredient?.trim().orEmpty()
            listOf(prefix, name).filter { it.isNotBlank() }.joinToString(" ")
        }
}

@Composable
fun CocktailDetailScreen(
    drink: Drink,
    lists: List<CocktailListEntity>,
    onBackToSearch: () -> Unit,
    onAddToLists: (List<Long>) -> Unit,
    onCreateList: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val ingredients = remember(drink) { cocktailIngredients(drink) }

    if (showDialog) {
        AddToListsDialog(
            lists = lists,
            onDismiss = { showDialog = false },
            onConfirm = { selectedIds ->
                showDialog = false
                onAddToLists(selectedIds)
            },
            onCreateList = onCreateList
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            IconButton(onClick = onBackToSearch) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour"
                )
            }
        }

        item {
            CocktailImage(
                imageUrl = drink.strDrinkThumb,
                contentDescription = drink.strDrink,
                height = 260
            )
        }

        item {
            Text(
                text = drink.strDrink ?: "Cocktail inconnu",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        item {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Text("Ajouter à une liste")
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    if (!drink.strCategory.isNullOrBlank()) {
                        Text("Catégorie : ${drink.strCategory}")
                    }
                    if (!drink.strAlcoholic.isNullOrBlank()) {
                        Text(
                            text = "Type : ${drink.strAlcoholic}",
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                    if (!drink.strGlass.isNullOrBlank()) {
                        Text(
                            text = "Verre : ${drink.strGlass}",
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ingrédients",
                        style = MaterialTheme.typography.titleLarge
                    )

                    ingredients.forEach { ingredient ->
                        Text(
                            text = "• $ingredient",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 24.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = drink.strInstructions ?: "Aucune instruction disponible.",
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
            }
        }
    }
}