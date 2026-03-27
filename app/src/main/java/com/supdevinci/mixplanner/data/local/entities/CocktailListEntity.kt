package com.supdevinci.mixplanner.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "cocktail_lists")
data class CocktailListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Date,
    val updatedAt: Date? = null,
    val deletedAt: Date? = null
)