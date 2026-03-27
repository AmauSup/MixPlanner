package com.supdevinci.mixplanner.viewmodel

import com.supdevinci.mixplanner.model.Drink

sealed interface RandomCocktailState {
    data object Idle : RandomCocktailState
    data object Loading : RandomCocktailState
    data class Success(val drinks: List<Drink>) : RandomCocktailState
    data class Error(val message: String) : RandomCocktailState
}