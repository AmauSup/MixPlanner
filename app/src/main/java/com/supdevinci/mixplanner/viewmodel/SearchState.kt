package com.supdevinci.mixplanner.viewmodel

import com.supdevinci.mixplanner.model.Drink

sealed interface SearchState {
    data object Idle : SearchState
    data object Loading : SearchState
    data class Success(
        val allDrinks: List<Drink>,
        val filteredDrinks: List<Drink>,
        val selectedDrink: Drink? = null
    ) : SearchState
    data class Error(val message: String) : SearchState
}