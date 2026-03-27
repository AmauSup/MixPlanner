


package com.supdevinci.mixplanner.service

import com.supdevinci.mixplanner.model.AlcoholicListResponse
import com.supdevinci.mixplanner.model.CategoryListResponse
import com.supdevinci.mixplanner.model.CocktailDetailResponse
import com.supdevinci.mixplanner.model.CocktailFilterResponse
import com.supdevinci.mixplanner.model.CocktailResponseAlias
import com.supdevinci.mixplanner.model.GlassListResponse
import com.supdevinci.mixplanner.model.IngredientListResponse
import com.supdevinci.mixplanner.model.IngredientResponse
import retrofit2.http.GET
import retrofit2.http.Query
interface CocktailApiService {

    @GET("random.php")
    suspend fun getRandomCocktail(): CocktailDetailResponse

    @GET("random.php")
    suspend fun getCocktail(): CocktailResponseAlias

    @GET("search.php")
    suspend fun searchCocktailsByName(
        @Query("s") name: String
    ): CocktailDetailResponse

    @GET("search.php")
    suspend fun searchCocktailsByFirstLetter(
        @Query("f") letter: String
    ): CocktailDetailResponse

    @GET("lookup.php")
    suspend fun getCocktailById(
        @Query("i") id: String
    ): CocktailDetailResponse

    @GET("filter.php")
    suspend fun filterByIngredient(
        @Query("i") ingredient: String
    ): CocktailFilterResponse

    @GET("filter.php")
    suspend fun filterByAlcoholic(
        @Query("a") alcoholic: String
    ): CocktailFilterResponse

    @GET("filter.php")
    suspend fun filterByCategory(
        @Query("c") category: String
    ): CocktailFilterResponse

    @GET("filter.php")
    suspend fun filterByGlass(
        @Query("g") glass: String
    ): CocktailFilterResponse


    @GET("search.php")
    suspend fun searchIngredientByName(
        @Query("i") name: String
    ): IngredientResponse

    @GET("lookup.php")
    suspend fun getIngredientById(
        @Query("iid") id: String
    ): IngredientResponse

    @GET("list.php?a=list")
    suspend fun getAlcoholicList(): AlcoholicListResponse

    @GET("list.php?c=list")
    suspend fun getCategoryList(): CategoryListResponse

    @GET("list.php?g=list")
    suspend fun getGlassList(): GlassListResponse

    @GET("list.php?i=list")
    suspend fun getIngredientList(): IngredientListResponse
}