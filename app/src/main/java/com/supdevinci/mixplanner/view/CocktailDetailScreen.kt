package com.supdevinci.mixplanner.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.supdevinci.mixplanner.data.local.entities.CocktailListEntity
import com.supdevinci.mixplanner.model.Drink

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
    ).filter { !it.first.isNullOrBlank() }

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        IconButton(onClick = onBackToSearch) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Retour"
            )
        }

        Button(onClick = { showDialog = true }) {
            Text("Ajouter à une liste")
        }

        Text(
            text = drink.strDrink ?: "Cocktail inconnu",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        CocktailImage(
            imageUrl = drink.strDrinkThumb,
            contentDescription = drink.strDrink,
            modifier = Modifier.padding(top = 12.dp)
        )

        if (!drink.strCategory.isNullOrBlank()) {
            Text(
                text = "Catégorie : ${drink.strCategory}",
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (!drink.strAlcoholic.isNullOrBlank()) {
            Text(
                text = "Type : ${drink.strAlcoholic}",
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (!drink.strGlass.isNullOrBlank()) {
            Text(
                text = "Verre : ${drink.strGlass}",
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (!drink.strInstructions.isNullOrBlank()) {
            Text(
                text = "Instructions : ${drink.strInstructions}",
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        if (ingredients.isNotEmpty()) {
            Text(
                text = "Ingrédients",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            ingredients.forEach { (ingredient, measure) ->
                Text(
                    text = "- ${measure.orEmpty()} ${ingredient.orEmpty()}",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}