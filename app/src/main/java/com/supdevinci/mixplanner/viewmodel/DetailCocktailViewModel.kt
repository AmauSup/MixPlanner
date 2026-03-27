package com.supdevinci.mixplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.mixplanner.service.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailCocktailViewModel : ViewModel() {

    private val _state = MutableStateFlow<DetailCocktailState>(DetailCocktailState.Loading)
    val state: StateFlow<DetailCocktailState> = _state.asStateFlow()

    fun loadCocktail(cocktailId: String) {
        if (cocktailId.isBlank()) {
            _state.value = DetailCocktailState.Error("Identifiant cocktail invalide")
            return
        }

        viewModelScope.launch {
            _state.value = DetailCocktailState.Loading

            try {
                val response = RetrofitInstance.api.getCocktailById(cocktailId)
                val drink = response.drinks?.firstOrNull()

                if (drink != null) {
                    _state.value = DetailCocktailState.Success(drink)
                } else {
                    _state.value = DetailCocktailState.Error("Cocktail introuvable")
                }
            } catch (e: Exception) {
                _state.value = DetailCocktailState.Error(
                    e.message ?: "Erreur réseau pendant le chargement du détail"
                )
            }
        }
    }
}