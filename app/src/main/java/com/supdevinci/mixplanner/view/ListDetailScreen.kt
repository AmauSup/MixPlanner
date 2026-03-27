package com.supdevinci.mixplanner.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.supdevinci.mixplanner.data.local.model.CocktailListWithCocktails
import com.supdevinci.mixplanner.viewmodel.CocktailListsViewModel

@Composable
fun ListDetailScreen(
    list: CocktailListWithCocktails,
    onBack: () -> Unit,
    onCocktailClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val viewModel: CocktailListsViewModel = viewModel()
    val ingredients by viewModel.aggregatedIngredients.collectAsStateWithLifecycle()
    val checkedItems = remember { mutableStateMapOf<String, Boolean>() }

    LaunchedEffect(list.list.id) {
        viewModel.loadIngredientsForList(list.list.id, list.cocktails)
    }

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
                var quantity by remember { mutableStateOf(0) }

                LaunchedEffect(cocktail.id, list.list.id) {
                    viewModel.getQuantity(list.list.id, cocktail.id) {
                        quantity = it
                    }
                }

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

                    CocktailImage(
                        imageUrl = cocktail.thumbUrl,
                        contentDescription = cocktail.name,
                        modifier = Modifier.padding(top = 8.dp)
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

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                viewModel.decreaseCocktailQuantity(list.list.id, cocktail.id)
                                quantity = (quantity - 1).coerceAtLeast(0)
                                viewModel.loadIngredientsForList(list.list.id, list.cocktails)
                            },
                            enabled = quantity > 0
                        ) {
                            Text("-")
                        }

                        Text(
                            text = "x$quantity",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )

                        Button(
                            onClick = {
                                viewModel.increaseCocktailQuantity(list.list.id, cocktail.id)
                                quantity += 1
                                viewModel.loadIngredientsForList(list.list.id, list.cocktails)
                            }
                        ) {
                            Text("+")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Ingrédients nécessaires",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val text = ingredients.toSortedMap()
                            .entries
                            .joinToString("\n") { "${it.key} x${it.value}" }

                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("ingredients", text))
                    }
                ) {
                    Text("Copier la liste")
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (ingredients.isEmpty()) {
                    Text("Chargement des ingrédients...")
                } else {
                    Column {
                        ingredients.toSortedMap().forEach { (ingredient, count) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        checkedItems[ingredient] = !(checkedItems[ingredient] ?: false)
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checkedItems[ingredient] ?: false,
                                    onCheckedChange = {
                                        checkedItems[ingredient] = it
                                    }
                                )

                                Text(
                                    text = "$ingredient x$count",
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}