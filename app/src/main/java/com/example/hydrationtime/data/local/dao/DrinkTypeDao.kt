package com.example.hydrationtime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hydrationtime.data.local.entities.DrinkType

@Dao
interface DrinkTypeDao {
    @Query("SELECT * FROM drink_types WHERE userId = :userId ORDER BY isCustom ASC, name ASC")
    fun getAllDrinkTypes(userId: String): LiveData<List<DrinkType>>

    @Query("SELECT * FROM drink_types WHERE userId = :userId ORDER BY isCustom ASC, name ASC")
    suspend fun getDrinkTypesList(userId: String): List<DrinkType>

    @Query("SELECT * FROM drink_types WHERE id = :id")
    suspend fun getDrinkTypeById(id: Int): DrinkType?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrinkType(drinkType: DrinkType): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(drinkTypes: List<DrinkType>)

    @Update
    suspend fun updateDrinkType(drinkType: DrinkType)

    @Delete
    suspend fun deleteDrinkType(drinkType: DrinkType)

    @Query("DELETE FROM drink_types WHERE userId = :userId AND isCustom = 1")
    suspend fun deleteAllCustomDrinks(userId: String)
}
