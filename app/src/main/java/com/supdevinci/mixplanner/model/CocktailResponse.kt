package com.supdevinci.mixplanner.model

typealias CocktailResponseAlias = CocktailDetailResponse

data class CocktailResponse(
    val drinks: List<Drink>?
)

data class CocktailDetailResponse(
    val drinks: List<Drink>?
)

data class CocktailFilterResponse(
    val drinks: List<DrinkSummary>?
)

data class DrinkSummary(
    val strDrink: String?,
    val strDrinkThumb: String?,
    val idDrink: String?
)

data class IngredientListResponse(
    val drinks: List<IngredientItem>?
)

data class IngredientItem(
    val strIngredient1: String?
)

data class CategoryListResponse(
    val drinks: List<CategoryItem>?
)

data class CategoryItem(
    val strCategory: String?
)

data class GlassListResponse(
    val drinks: List<GlassItem>?
)

data class GlassItem(
    val strGlass: String?
)

data class AlcoholicListResponse(
    val drinks: List<AlcoholicItem>?
)

data class AlcoholicItem(
    val strAlcoholic: String?
)

data class IngredientResponse(
    val ingredients: List<Ingredient>?
)

data class Ingredient(
    val idIngredient: String?,
    val strIngredient: String?,
    val strDescription: String?,
    val strType: String?,
    val strAlcohol: String?,
    val strABV: String?
)