package com.supdevinci.mixplanner.viewmodel

import com.supdevinci.mixplanner.data.local.model.CocktailListWithCocktails

sealed interface CocktailListsState {
    data object Loading : CocktailListsState
    data object Empty : CocktailListsState
    data class Success(val lists: List<CocktailListWithCocktails>) : CocktailListsState
    data class Error(val message: String) : CocktailListsState
}