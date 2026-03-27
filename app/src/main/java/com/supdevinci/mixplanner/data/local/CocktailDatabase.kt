package com.supdevinci.mixplanner.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.supdevinci.mixplanner.data.local.dao.CocktailDao
import com.supdevinci.mixplanner.data.local.dao.CocktailListDao
import com.supdevinci.mixplanner.data.local.entities.CocktailEntity
import com.supdevinci.mixplanner.data.local.entities.CocktailListEntity
import com.supdevinci.mixplanner.data.local.entities.ListCocktailCrossRef
import com.supdevinci.mixplanner.data.local.entities.SavedCocktailEntity


@Database(
    entities = [
        CocktailEntity::class,
        CocktailListEntity::class,
        SavedCocktailEntity::class,
        ListCocktailCrossRef::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CocktailDatabase : RoomDatabase() {

    abstract fun cocktailDao(): CocktailDao
    abstract fun cocktailListDao(): CocktailListDao

    companion object {
        @Volatile
        private var INSTANCE: CocktailDatabase? = null

        fun getDatabase(context: Context): CocktailDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CocktailDatabase::class.java,
                    "cocktail_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}