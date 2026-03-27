package com.supdevinci.mixplanner.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.mixplanner.data.local.CocktailDatabase
import com.supdevinci.mixplanner.data.local.entities.CocktailListEntity
import com.supdevinci.mixplanner.data.local.entities.ListCocktailCrossRef
import com.supdevinci.mixplanner.data.local.entities.SavedCocktailEntity
import com.supdevinci.mixplanner.data.local.model.CocktailListWithCocktails
import com.supdevinci.mixplanner.model.Drink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import com.supdevinci.mixplanner.service.RetrofitInstance

class CocktailListsViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = CocktailDatabase.getDatabase(application).cocktailListDao()

    private val _state = MutableStateFlow<CocktailListsState>(CocktailListsState.Loading)
    val state: StateFlow<CocktailListsState> = _state.asStateFlow()

    private val _visibleLists = MutableStateFlow<List<CocktailListEntity>>(emptyList())
    val visibleLists: StateFlow<List<CocktailListEntity>> = _visibleLists.asStateFlow()

    private val _aggregatedIngredients =
        MutableStateFlow<Map<String, Int>>(emptyMap())
    val aggregatedIngredients: StateFlow<Map<String, Int>> =
        _aggregatedIngredients.asStateFlow()
    init {
        viewModelScope.launch {
            dao.getVisibleListsWithCocktails().collect { lists ->
                _state.value = when {
                    lists.isEmpty() -> CocktailListsState.Empty
                    else -> CocktailListsState.Success(lists)
                }
            }
        }
        viewModelScope.launch {
            dao.getVisibleLists().collect { lists ->
                _visibleLists.value = lists
            }
        }

    }

    fun addDrinkToLists(listIds: List<Long>, drink: Drink) {
        val remoteId = drink.idDrink
        val name = drink.strDrink

        if (remoteId.isNullOrBlank() || name.isNullOrBlank()) {
            _state.value = CocktailListsState.Error("Cocktail invalide")
            return
        }

        if (listIds.isEmpty()) {
            _state.value = CocktailListsState.Error("Choisis au moins une liste")
            return
        }

        viewModelScope.launch {
            try {
                val now = Date()

                val existing = dao.getSavedCocktailByRemoteId(remoteId)

                val savedCocktailId = if (existing != null) {
                    dao.updateSavedCocktail(
                        existing.copy(
                            name = name,
                            thumbUrl = drink.strDrinkThumb,
                            category = drink.strCategory,
                            alcoholic = drink.strAlcoholic,
                            glass = drink.strGlass,
                            instructions = drink.strInstructions,
                            updatedAt = now,
                            deletedAt = null
                        )
                    )
                    existing.id
                } else {
                    dao.insertSavedCocktail(
                        SavedCocktailEntity(
                            remoteId = remoteId,
                            name = name,
                            thumbUrl = drink.strDrinkThumb,
                            category = drink.strCategory,
                            alcoholic = drink.strAlcoholic,
                            glass = drink.strGlass,
                            instructions = drink.strInstructions,
                            createdAt = now,
                            updatedAt = now,
                            deletedAt = null
                        )
                    )
                }

                listIds.forEach { listId ->
                    dao.insertCrossRef(
                        ListCocktailCrossRef(
                            listId = listId,
                            savedCocktailId = savedCocktailId,
                            quantity = 1,
                            createdAt = now,
                            updatedAt = now,
                            deletedAt = null
                        )
                    )
                }
            } catch (e: Exception) {
                _state.value = CocktailListsState.Error(
                    e.message ?: "Erreur lors de l'ajout du cocktail aux listes"
                )
            }
        }
    }

    fun createList(name: String) {
        if (name.isBlank()) {
            _state.value = CocktailListsState.Error("Le nom de la liste est obligatoire")
            return
        }

        viewModelScope.launch {
            try {
                val now = Date()
                dao.insertList(
                    CocktailListEntity(
                        name = name.trim(),
                        createdAt = now,
                        updatedAt = now,
                        deletedAt = null
                    )
                )
            } catch (e: Exception) {
                _state.value = CocktailListsState.Error(
                    e.message ?: "Erreur lors de la création de la liste"
                )
            }
        }
    }

    fun renameList(listId: Long, newName: String) {
        if (newName.isBlank()) {
            _state.value = CocktailListsState.Error("Le nouveau nom est obligatoire")
            return
        }

        viewModelScope.launch {
            try {
                dao.renameList(listId, newName.trim(), Date())
            } catch (e: Exception) {
                _state.value = CocktailListsState.Error(
                    e.message ?: "Erreur lors du renommage"
                )
            }
        }
    }

    fun softDeleteList(listId: Long) {
        viewModelScope.launch {
            try {
                dao.softDeleteList(listId, Date())
            } catch (e: Exception) {
                _state.value = CocktailListsState.Error(
                    e.message ?: "Erreur lors de la suppression"
                )
            }
        }
    }

    fun addDrinkToList(listId: Long, drink: Drink) {
        val remoteId = drink.idDrink
        val name = drink.strDrink

        if (remoteId.isNullOrBlank() || name.isNullOrBlank()) {
            _state.value = CocktailListsState.Error("Cocktail invalide")
            return
        }

        viewModelScope.launch {
            try {
                val now = Date()

                val existing = dao.getSavedCocktailByRemoteId(remoteId)

                val savedCocktailId = if (existing != null) {
                    dao.updateSavedCocktail(
                        existing.copy(
                            name = name,
                            thumbUrl = drink.strDrinkThumb,
                            category = drink.strCategory,
                            alcoholic = drink.strAlcoholic,
                            glass = drink.strGlass,
                            instructions = drink.strInstructions,
                            updatedAt = now
                        )
                    )
                    existing.id
                } else {
                    dao.insertSavedCocktail(
                        SavedCocktailEntity(
                            remoteId = remoteId,
                            name = name,
                            thumbUrl = drink.strDrinkThumb,
                            category = drink.strCategory,
                            alcoholic = drink.strAlcoholic,
                            glass = drink.strGlass,
                            instructions = drink.strInstructions,
                            createdAt = now,
                            updatedAt = now,
                            deletedAt = null
                        )
                    )
                }

                dao.insertCrossRef(
                    ListCocktailCrossRef(
                        listId = listId,
                        savedCocktailId = savedCocktailId,
                        quantity = 1,
                        createdAt = now,
                        updatedAt = now,
                        deletedAt = null
                    )
                )
            } catch (e: Exception) {
                _state.value = CocktailListsState.Error(
                    e.message ?: "Erreur lors de l'ajout du cocktail à la liste"
                )
            }
        }
    }
    fun getListById(listId: Long): CocktailListWithCocktails? {
        val current = _state.value
        return if (current is CocktailListsState.Success) {
            current.lists.firstOrNull { it.list.id == listId }
        } else {
            null
        }
    }

    fun loadIngredientsForList(listId: Long,drinks: List<SavedCocktailEntity>) {
        viewModelScope.launch {
            val counts = mutableMapOf<String, Int>()

            for (cocktail in drinks) {
                val id = cocktail.remoteId

                if (id.isBlank()) continue

                try {
                    val response = RetrofitInstance.api.getCocktailById(id)
                    val drink = response.drinks?.firstOrNull() ?: continue

                    val ingredients = listOf(
                        drink.strIngredient1,
                        drink.strIngredient2,
                        drink.strIngredient3,
                        drink.strIngredient4,
                        drink.strIngredient5,
                        drink.strIngredient6,
                        drink.strIngredient7,
                        drink.strIngredient8,
                        drink.strIngredient9,
                        drink.strIngredient10,
                        drink.strIngredient11,
                        drink.strIngredient12,
                        drink.strIngredient13,
                        drink.strIngredient14,
                        drink.strIngredient15
                    )

                    ingredients
                        .filterNotNull()
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                        .forEach { ingredient ->
                            val crossRef = dao.getCrossRef(listId, cocktail.id)
                            val quantity = crossRef?.quantity ?: 1

                            counts[ingredient] = (counts[ingredient] ?: 0) + quantity
                        }

                } catch (_: Exception) {
                }
            }

            _aggregatedIngredients.value = counts
        }
    }

    fun increaseCocktailQuantity(listId: Long, savedCocktailId: Long) {
        viewModelScope.launch {
            try {
                val crossRef = dao.getCrossRef(listId, savedCocktailId) ?: return@launch
                dao.updateCrossRefQuantity(
                    listId = listId,
                    savedCocktailId = savedCocktailId,
                    quantity = crossRef.quantity + 1,
                    updatedAt = Date()
                )
            } catch (e: Exception) {
                _state.value = CocktailListsState.Error(
                    e.message ?: "Erreur lors de l'augmentation de la quantité"
                )
            }
        }
    }

    fun decreaseCocktailQuantity(listId: Long, savedCocktailId: Long) {
        viewModelScope.launch {
            try {
                val crossRef = dao.getCrossRef(listId, savedCocktailId) ?: return@launch
                val newQuantity = (crossRef.quantity - 1).coerceAtLeast(0)

                dao.updateCrossRefQuantity(
                    listId = listId,
                    savedCocktailId = savedCocktailId,
                    quantity = newQuantity,
                    updatedAt = Date()
                )
            } catch (e: Exception) {
                _state.value = CocktailListsState.Error(
                    e.message ?: "Erreur lors de la diminution de la quantité"
                )
            }
        }
    }

    fun getQuantity(listId: Long, savedCocktailId: Long, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val crossRef = dao.getCrossRef(listId, savedCocktailId)
            onResult(crossRef?.quantity ?: 0)
        }
    }
}