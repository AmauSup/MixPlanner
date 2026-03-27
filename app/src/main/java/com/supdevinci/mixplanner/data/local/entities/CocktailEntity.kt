package com.supdevinci.mixplanner.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cocktail")
data class CocktailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val instructions: String,
    val isFavorite: Boolean = false,
    val createdAt: Date,
    val updatedAt: Date? = null,
    val deletedAt: Date? = null
)