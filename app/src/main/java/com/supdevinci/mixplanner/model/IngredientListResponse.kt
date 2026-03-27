package com.supdevinci.mixplanner.model

data class IngredientListResponse(
    val drinks: List<IngredientItem>?
)

data class IngredientItem(
    val strIngredient1: String?
)