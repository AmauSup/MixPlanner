package com.supdevinci.mixplanner.data.local.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.supdevinci.mixplanner.data.local.entities.CocktailListEntity
import com.supdevinci.mixplanner.data.local.entities.ListCocktailCrossRef
import com.supdevinci.mixplanner.data.local.entities.SavedCocktailEntity

data class CocktailListWithCocktails(
    @Embedded val list: CocktailListEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ListCocktailCrossRef::class,
            parentColumn = "listId",
            entityColumn = "savedCocktailId"
        )
    )
    val cocktails: List<SavedCocktailEntity>
)