package com.supdevinci.mixplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.mixplanner.model.Drink
import com.supdevinci.mixplanner.service.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RandomCocktailViewModel : ViewModel() {

    private val _state = MutableStateFlow<RandomCocktailState>(RandomCocktailState.Idle)
    val state: StateFlow<RandomCocktailState> = _state.asStateFlow()

    init {
        getRandomCocktails()
    }

    fun getRandomCocktails() {
        viewModelScope.launch {
            _state.value = RandomCocktailState.Loading

            var lastErrorMessage = "Une erreur réseau est survenue"

            repeat(3) { globalAttempt ->
                try {
                    val distinctIds = linkedSetOf<String>()
                    val results = mutableListOf<Drink>()

                    repeat(12) {
                        val response = RetrofitInstance.api.getRandomCocktail()
                        val drink = response.drinks?.firstOrNull()

                        if (drink != null) {
                            val id = drink.idDrink
                            if (!id.isNullOrBlank() && distinctIds.add(id)) {
                                results.add(drink)
                            }
                        }

                        if (results.size == 3) {
                            _state.value = RandomCocktailState.Success(results)
                            return@launch
                        }

                        delay(120)
                    }

                    if (results.isNotEmpty()) {
                        _state.value = RandomCocktailState.Success(results)
                        return@launch
                    } else {
                        lastErrorMessage = "Aucun cocktail trouvé"
                    }
                } catch (e: Exception) {
                    lastErrorMessage = e.message ?: "Une erreur réseau est survenue"
                }

                if (globalAttempt < 2) {
                    delay(700)
                }
            }

            _state.value = RandomCocktailState.Error(lastErrorMessage)
        }
    }
}