package com.supdevinci.mixplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.mixplanner.data.local.CocktailDatabase
import com.supdevinci.mixplanner.data.local.entities.CocktailEntity
import com.supdevinci.mixplanner.service.RetrofitInstance
import com.supdevinci.mixplanner.viewmodel.CocktailState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class CocktailViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = CocktailDatabase.getDatabase(application).cocktailDao()

    private val _state = MutableStateFlow<CocktailState>(CocktailState.Loading)
    val state: StateFlow<CocktailState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAllVisibleCocktails().collect { cocktails ->
                _state.value = when {
                    cocktails.isEmpty() -> CocktailState.Empty
                    else -> CocktailState.Success(cocktails)
                }
            }
        }
    }

    fun addCocktail(name: String, instructions: String) {
        if (name.isBlank() || instructions.isBlank()) {
            _state.value = CocktailState.Error("Le nom et les instructions sont obligatoires")
            return
        }

        viewModelScope.launch {
            try {
                val now = Date()
                dao.insert(
                    CocktailEntity(
                        name = name.trim(),
                        instructions = instructions.trim(),
                        isFavorite = false,
                        createdAt = now,
                        updatedAt = now,
                        deletedAt = null
                    )
                )
            } catch (e: Exception) {
                _state.value = CocktailState.Error(
                    e.message ?: "Erreur lors de l'ajout du cocktail"
                )
            }
        }
    }

    fun toggleFavorite(cocktail: CocktailEntity) {
        viewModelScope.launch {
            try {
                dao.update(
                    cocktail.copy(
                        isFavorite = !cocktail.isFavorite,
                        updatedAt = Date()
                    )
                )
            } catch (e: Exception) {
                _state.value = CocktailState.Error(
                    e.message ?: "Erreur lors de la mise à jour du favori"
                )
            }
        }
    }

    fun archiveCocktail(id: Long) {
        viewModelScope.launch {
            try {
                dao.softDelete(id, Date())
            } catch (e: Exception) {
                _state.value = CocktailState.Error(
                    e.message ?: "Erreur lors de l'archivage du cocktail"
                )
            }
        }
    }
}

sealed interface CocktailState {
    data object Loading : CocktailState
    data object Empty : CocktailState
    data class Success(val cocktails: List<CocktailEntity>) : CocktailState
    data class Error(val message: String) : CocktailState
}