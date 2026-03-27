package com.supdevinci.mixplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.mixplanner.service.RetrofitInstance
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RandomCocktailViewModel : ViewModel() {

    private val _state = MutableStateFlow<RandomCocktailState>(RandomCocktailState.Loading)
    val state: StateFlow<RandomCocktailState> = _state.asStateFlow()

    init {
        getRandomCocktail()
    }

    fun getRandomCocktail() {
        viewModelScope.launch {
            _state.value = RandomCocktailState.Loading

            var lastErrorMessage = "Une erreur réseau est survenue"

            repeat(3) { attempt ->
                try {
                    val response = RetrofitInstance.api.getRandomCocktail()
                    val drink = response.drinks?.firstOrNull()

                    if (drink != null) {
                        _state.value = RandomCocktailState.Success(drink)
                        return@launch
                    } else {
                        lastErrorMessage = "Aucun cocktail trouvé"
                    }
                } catch (e: Exception) {
                    android.util.Log.e("RandomCocktailVM", "Tentative ${attempt + 1}", e)
                    lastErrorMessage = e.message ?: "Une erreur réseau est survenue"
                }

                if (attempt < 2) {
                    delay(1200)
                }
            }

            _state.value = RandomCocktailState.Error(lastErrorMessage)
        }
    }
}