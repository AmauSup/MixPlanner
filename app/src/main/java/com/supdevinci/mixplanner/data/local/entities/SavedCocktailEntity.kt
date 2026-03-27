package com.supdevinci.mixplanner.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "saved_cocktails")
data class SavedCocktailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: String,
    val name: String,
    val thumbUrl: String? = null,
    val category: String? = null,
    val alcoholic: String? = null,
    val glass: String? = null,
    val instructions: String? = null,
    val createdAt: Date,
    val updatedAt: Date? = null,
    val deletedAt: Date? = null
)