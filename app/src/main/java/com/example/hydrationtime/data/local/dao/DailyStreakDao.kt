package com.example.hydrationtime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hydrationtime.data.local.entities.DailyStreak

@Dao
interface DailyStreakDao {
    @Query("SELECT * FROM daily_streaks WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getStreaksInRange(userId: String, startDate: String, endDate: String): LiveData<List<DailyStreak>>

    @Query("SELECT * FROM daily_streaks WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getStreaksInRangeSync(userId: String, startDate: String, endDate: String): List<DailyStreak>

    @Query("SELECT * FROM daily_streaks WHERE userId = :userId AND date = :date")
    suspend fun getStreakByDate(userId: String, date: String): DailyStreak?

    @Query("SELECT COUNT(*) FROM daily_streaks WHERE userId = :userId AND goalAchieved = 1 AND date BETWEEN :startDate AND :endDate")
    suspend fun getAchievedDaysCount(userId: String, startDate: String, endDate: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreak(streak: DailyStreak)

    @Update
    suspend fun updateStreak(streak: DailyStreak)

    @Query("DELETE FROM daily_streaks WHERE userId = :userId")
    suspend fun deleteAllStreaks(userId: String)
}
