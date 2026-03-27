package com.supdevinci.mixplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.supdevinci.mixplanner.data.local.entities.CocktailListEntity
import com.supdevinci.mixplanner.data.local.entities.ListCocktailCrossRef
import com.supdevinci.mixplanner.data.local.entities.SavedCocktailEntity
import com.supdevinci.mixplanner.data.local.model.CocktailListWithCocktails
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface CocktailListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: CocktailListEntity): Long

    @Update
    suspend fun updateList(list: CocktailListEntity)

    @Query("UPDATE cocktail_lists SET name = :newName, updatedAt = :updatedAt WHERE id = :listId")
    suspend fun renameList(listId: Long, newName: String, updatedAt: Date)

    @Query("UPDATE cocktail_lists SET deletedAt = :deletedAt WHERE id = :listId")
    suspend fun softDeleteList(listId: Long, deletedAt: Date)

    @Query("SELECT * FROM cocktail_lists WHERE deletedAt IS NULL ORDER BY createdAt DESC")
    fun getVisibleLists(): Flow<List<CocktailListEntity>>

    @Query("SELECT * FROM cocktail_lists WHERE id = :listId LIMIT 1")
    suspend fun getListById(listId: Long): CocktailListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedCocktail(cocktail: SavedCocktailEntity): Long

    @Update
    suspend fun updateSavedCocktail(cocktail: SavedCocktailEntity)

    @Query("SELECT * FROM saved_cocktails WHERE remoteId = :remoteId AND deletedAt IS NULL LIMIT 1")
    suspend fun getSavedCocktailByRemoteId(remoteId: String): SavedCocktailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: ListCocktailCrossRef)

    @Query("""
        UPDATE list_cocktail_cross_ref
        SET deletedAt = :deletedAt, updatedAt = :deletedAt
        WHERE listId = :listId AND savedCocktailId = :savedCocktailId
    """)
    suspend fun softDeleteCocktailFromList(
        listId: Long,
        savedCocktailId: Long,
        deletedAt: Date
    )

    @Query("""
        UPDATE list_cocktail_cross_ref
        SET quantity = :quantity, updatedAt = :updatedAt
        WHERE listId = :listId AND savedCocktailId = :savedCocktailId
    """)
    suspend fun updateCrossRefQuantity(
        listId: Long,
        savedCocktailId: Long,
        quantity: Int,
        updatedAt: Date
    )

    @Query("""
        SELECT * FROM list_cocktail_cross_ref
        WHERE listId = :listId AND savedCocktailId = :savedCocktailId
        LIMIT 1
    """)
    suspend fun getCrossRef(
        listId: Long,
        savedCocktailId: Long
    ): ListCocktailCrossRef?

    @Transaction
    @Query("SELECT * FROM cocktail_lists WHERE deletedAt IS NULL ORDER BY createdAt DESC")
    fun getVisibleListsWithCocktails(): Flow<List<CocktailListWithCocktails>>

    @Transaction
    @Query("SELECT * FROM cocktail_lists WHERE id = :listId AND deletedAt IS NULL LIMIT 1")
    fun getListDetails(listId: Long): Flow<CocktailListWithCocktails?>
}