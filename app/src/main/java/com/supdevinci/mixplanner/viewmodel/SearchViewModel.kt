package com.supdevinci.mixplanner.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.supdevinci.mixplanner.model.Drink
import com.supdevinci.mixplanner.service.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _state = MutableStateFlow<SearchState>(SearchState.Idle)
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val _alcoholicOptions = MutableStateFlow<List<String>>(emptyList())
    val alcoholicOptions: StateFlow<List<String>> = _alcoholicOptions.asStateFlow()

    private val _categoryOptions = MutableStateFlow<List<String>>(emptyList())
    val categoryOptions: StateFlow<List<String>> = _categoryOptions.asStateFlow()

    private val _glassOptions = MutableStateFlow<List<String>>(emptyList())
    val glassOptions: StateFlow<List<String>> = _glassOptions.asStateFlow()

    private var currentBaseResults: List<Drink> = emptyList()
    private var allCatalog: List<Drink> = emptyList()

    private var currentAlcoholicFilter: String? = null
    private var currentCategoryFilter: String? = null
    private var currentGlassFilter: String? = null

    init {
        loadFilterOptions()
    }

    private fun loadFilterOptions() {
        viewModelScope.launch {
            try {
                val alcoholic = RetrofitInstance.api.getAlcoholicList()
                    .drinks
                    .orEmpty()
                    .mapNotNull { it.strAlcoholic }
                    .distinct()

                val categories = RetrofitInstance.api.getCategoryList()
                    .drinks
                    .orEmpty()
                    .mapNotNull { it.strCategory }
                    .distinct()

                val glasses = RetrofitInstance.api.getGlassList()
                    .drinks
                    .orEmpty()
                    .mapNotNull { it.strGlass }
                    .distinct()

                Log.d("SearchViewModel", "alcoholic = $alcoholic")
                Log.d("SearchViewModel", "categories = $categories")
                Log.d("SearchViewModel", "glasses = $glasses")

                _alcoholicOptions.value = alcoholic
                _categoryOptions.value = categories
                _glassOptions.value = glasses
            } catch (e: Exception) {
                Log.e("SearchViewModel", "Erreur loadFilterOptions", e)
            }
        }
    }

    fun runSearch(query: String, byIngredient: Boolean) {
        val cleanQuery = query.trim()

        if (cleanQuery.isBlank()) {
            browseAllCocktails()
            return
        }

        if (byIngredient) {
            searchCocktailsByIngredient(cleanQuery)
        } else {
            searchCocktailsByName(cleanQuery)
        }
    }

    fun browseAllCocktails() {
        viewModelScope.launch {
            _state.value = SearchState.Loading

            try {
                ensureCatalogLoaded()

                if (allCatalog.isEmpty()) {
                    _state.value = SearchState.Error("Impossible de charger la liste des cocktails")
                    return@launch
                }

                currentBaseResults = allCatalog
                publishFilteredResults()
            } catch (e: Exception) {
                _state.value = SearchState.Error(
                    e.message ?: "Erreur pendant le chargement du catalogue"
                )
            }
        }
    }

    fun searchCocktailsByName(query: String) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) {
            browseAllCocktails()
            return
        }

        viewModelScope.launch {
            _state.value = SearchState.Loading

            try {
                val response = RetrofitInstance.api.searchCocktailsByName(cleanQuery)
                val drinks = response.drinks.orEmpty()

                if (drinks.isNotEmpty()) {
                    currentBaseResults = drinks
                    publishFilteredResults()
                } else {
                    currentBaseResults = emptyList()
                    _state.value = SearchState.Error(
                        "Aucun cocktail ne correspond à \"$cleanQuery\"."
                    )
                }
            } catch (e: Exception) {
                _state.value = SearchState.Error(
                    "Erreur réseau pendant la recherche de \"$cleanQuery\"."
                )
            }
        }
    }

    fun searchCocktailsByIngredient(query: String) {
        val cleanQuery = query.trim()
        if (cleanQuery.isBlank()) {
            browseAllCocktails()
            return
        }

        viewModelScope.launch {
            _state.value = SearchState.Loading

            try {
                val apiIngredient = normalizeIngredientQuery(cleanQuery)
                val response = RetrofitInstance.api.filterByIngredient(apiIngredient)
                val summaries = response.drinks.orEmpty()

                if (summaries.isEmpty()) {
                    _state.value = SearchState.Error(
                        "Aucun cocktail trouvé avec l’ingrédient \"$cleanQuery\". Vérifie l’orthographe ou essaie en anglais."
                    )
                    return@launch
                }

                val detailedDrinks = mutableListOf<Drink>()

                for (id in summaries.mapNotNull { it.idDrink }.distinct()) {
                    try {
                        val drink = RetrofitInstance.api.getCocktailById(id).drinks?.firstOrNull()
                        if (drink != null) {
                            detailedDrinks.add(drink)
                        }
                    } catch (_: Exception) {
                    }
                }

                if (detailedDrinks.isNotEmpty()) {
                    currentBaseResults = detailedDrinks
                    publishFilteredResults()
                } else {
                    _state.value = SearchState.Error(
                        "Des cocktails ont été trouvés pour \"$cleanQuery\", mais leurs détails n’ont pas pu être chargés."
                    )
                }
            } catch (e: Exception) {
                    _state.value = SearchState.Error(
                        "Erreur réseau pendant la recherche de \"$cleanQuery\"."
                    )
            }
        }
    }

    private suspend fun ensureCatalogLoaded() {
        if (allCatalog.isNotEmpty()) return

        val results = mutableListOf<Drink>()

        for (letter in 'a'..'z') {
            try {
                val response = RetrofitInstance.api.searchCocktailsByFirstLetter(letter.toString())
                results.addAll(response.drinks.orEmpty())
            } catch (_: Exception) {
            }
        }

        allCatalog = results
            .distinctBy { it.idDrink ?: it.strDrink.orEmpty() }
    }

    private fun normalizeIngredientQuery(query: String): String {
        return when (query.trim().lowercase()) {
            "citron" -> "Lemon"
            "citron vert" -> "Lime"
            "rhum" -> "Rum"
            "whisky" -> "Whiskey"
            else -> query.trim()
        }
    }

    fun setAlcoholicFilter(value: String?) {
        currentAlcoholicFilter = value
        if (currentBaseResults.isEmpty()) {
            browseAllCocktails()
        } else {
            publishFilteredResults()
        }
    }

    fun setCategoryFilter(value: String?) {
        currentCategoryFilter = value
        if (currentBaseResults.isEmpty()) {
            browseAllCocktails()
        } else {
            publishFilteredResults()
        }
    }

    fun setGlassFilter(value: String?) {
        currentGlassFilter = value
        if (currentBaseResults.isEmpty()) {
            browseAllCocktails()
        } else {
            publishFilteredResults()
        }
    }

    fun clearAllFilters() {
        clearFiltersOnly()
        if (currentBaseResults.isEmpty()) {
            browseAllCocktails()
        } else {
            publishFilteredResults()
        }
    }

    fun loadCocktailDetails(id: String?) {
        if (id.isNullOrBlank()) {
            _state.value = SearchState.Error("Impossible d'ouvrir la fiche cocktail")
            return
        }

        val current = _state.value
        val currentFiltered =
            if (current is SearchState.Success) current.filteredDrinks else emptyList()

        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getCocktailById(id)
                val detailedDrink = response.drinks?.firstOrNull()

                if (detailedDrink != null) {
                    _state.value = SearchState.Success(
                        allDrinks = currentBaseResults,
                        filteredDrinks = currentFiltered,
                        selectedDrink = detailedDrink
                    )
                } else {
                    _state.value = SearchState.Error("Fiche cocktail introuvable")
                }
            } catch (e: Exception) {
                _state.value = SearchState.Error(
                    e.message ?: "Erreur réseau pendant le chargement de la fiche"
                )
            }
        }
    }

    fun selectDrink(drink: Drink) {
        val current = _state.value
        if (current is SearchState.Success) {
            _state.value = current.copy(selectedDrink = drink)
        }
    }

    fun clearDetails() {
        val current = _state.value
        if (current is SearchState.Success) {
            _state.value = current.copy(selectedDrink = null)
        }
    }

    private fun clearFiltersOnly() {
        currentAlcoholicFilter = null
        currentCategoryFilter = null
        currentGlassFilter = null
    }

    private fun publishFilteredResults() {
        if (currentBaseResults.isEmpty()) {
            _state.value = SearchState.Error("Aucun résultat à filtrer")
            return
        }

        val filtered = currentBaseResults.filter { drink ->
            matchesAlcoholic(drink) &&
                    matchesCategory(drink) &&
                    matchesGlass(drink)
        }

        _state.value = if (filtered.isEmpty()) {
            SearchState.Error("Aucun cocktail ne correspond aux filtres sélectionnés")
        } else {
            SearchState.Success(
                allDrinks = currentBaseResults,
                filteredDrinks = filtered,
                selectedDrink = null
            )
        }
    }

    private fun matchesAlcoholic(drink: Drink): Boolean {
        val filter = currentAlcoholicFilter ?: return true
        return drink.strAlcoholic.equals(filter, ignoreCase = true)
    }

    private fun matchesCategory(drink: Drink): Boolean {
        val filter = currentCategoryFilter ?: return true
        return drink.strCategory.equals(filter, ignoreCase = true)
    }

    private fun matchesGlass(drink: Drink): Boolean {
        val filter = currentGlassFilter ?: return true
        return drink.strGlass.equals(filter, ignoreCase = true)
    }
}