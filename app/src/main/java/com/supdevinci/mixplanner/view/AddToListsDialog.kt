package com.supdevinci.mixplanner.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.supdevinci.mixplanner.data.local.entities.CocktailListEntity

@Composable
fun AddToListsDialog(
    lists: List<CocktailListEntity>,
    onDismiss: () -> Unit,
    onConfirm: (List<Long>) -> Unit,
    onCreateList: (String) -> Unit
) {
    val selectedIds = remember { mutableStateListOf<Long>() }
    var newListName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add to one or more lists") },
        text = {
            Column {
                OutlinedTextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("New list name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (newListName.isNotBlank()) {
                            onCreateList(newListName.trim())
                            newListName = ""
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Créer une liste")
                }

                if (lists.isEmpty()) {
                    Text(
                        text = "Aucune liste disponible.",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        items(lists, key = { it.id }) { list ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = selectedIds.contains(list.id),
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            if (!selectedIds.contains(list.id)) {
                                                selectedIds.add(list.id)
                                            }
                                        } else {
                                            selectedIds.remove(list.id)
                                        }
                                    }
                                )

                                Text(
                                    text = list.name,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(selectedIds.toList()) },
                enabled = selectedIds.isNotEmpty()
            ) {
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}