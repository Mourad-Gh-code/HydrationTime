package com.example.hydrationtime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hydrationtime.data.local.entities.UserPreferences

@Dao
interface UserPreferencesDao {
    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    fun getUserPreferences(userId: String): LiveData<UserPreferences?>

    @Query("SELECT * FROM user_preferences WHERE userId = :userId")
    suspend fun getUserPreferencesSync(userId: String): UserPreferences?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreferences(preferences: UserPreferences)

    @Update
    suspend fun updatePreferences(preferences: UserPreferences)

    @Query("UPDATE user_preferences SET isDarkMode = :isDarkMode WHERE userId = :userId")
    suspend fun updateTheme(userId: String, isDarkMode: Boolean)

    @Query("UPDATE user_preferences SET language = :language WHERE userId = :userId")
    suspend fun updateLanguage(userId: String, language: String)

    @Query("UPDATE user_preferences SET dailyGoalMl = :goalMl WHERE userId = :userId")
    suspend fun updateDailyGoal(userId: String, goalMl: Float)
}
