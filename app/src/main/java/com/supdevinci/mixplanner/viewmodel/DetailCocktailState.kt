package com.supdevinci.mixplanner.viewmodel

import com.supdevinci.mixplanner.model.Drink

sealed interface DetailCocktailState {
    data object Loading : DetailCocktailState
    data class Success(val drink: Drink) : DetailCocktailState
    data class Error(val message: String) : DetailCocktailState
}