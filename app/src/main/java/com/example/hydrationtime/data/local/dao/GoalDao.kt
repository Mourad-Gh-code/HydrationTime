package com.example.hydrationtime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.android.gms.fitness.data.Goal
/**
 * GoalDao - Opérations sur la table goals
 */
@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllGoals(userId: String): LiveData<List<Goal>>

    @Query("SELECT * FROM goals WHERE userId = :userId AND date = :date LIMIT 1")
    suspend fun getGoalByDate(userId: String, date: String): Goal?

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("DELETE FROM goals WHERE userId = :userId")
    suspend fun deleteAllUserGoals(userId: String)
}