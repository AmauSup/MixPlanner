package com.supdevinci.mixplanner.data.local.entities

import androidx.room.Entity
import java.util.Date


@Entity(
    tableName = "list_cocktail_cross_ref",
    primaryKeys = ["listId", "savedCocktailId"]
)
data class ListCocktailCrossRef(
    val listId: Long,
    val savedCocktailId: Long,
    val quantity: Int = 1,
    val createdAt: Date,
    val updatedAt: Date,
    val deletedAt: Date? = null
)
