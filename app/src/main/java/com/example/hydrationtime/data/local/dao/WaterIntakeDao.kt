package com.example.hydrationtime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hydrationtime.data.local.entities.*

/**
 * WaterIntakeDao - Opérations sur la table water_intake
 */
@Dao
interface WaterIntakeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterIntake(waterIntake: WaterIntake)

    @Query("SELECT * FROM water_intake WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllIntakes(userId: String): LiveData<List<WaterIntake>>

    @Query("SELECT * FROM water_intake WHERE userId = :userId AND date = :date")
    fun getIntakesByDate(userId: String, date: String): LiveData<List<WaterIntake>>

    @Query("""
        SELECT date, SUM(amount) as totalAmount 
        FROM water_intake 
        WHERE userId = :userId AND date >= :startDate 
        GROUP BY date 
        ORDER BY date ASC
    """)
    fun getWeeklyIntakes(userId: String, startDate: String): LiveData<List<DailyIntake>>

    @Query("SELECT SUM(amount) FROM water_intake WHERE userId = :userId AND date = :date")
    fun getTodayTotal(userId: String, date: String): LiveData<Float?>

    @Query("SELECT SUM(amount) FROM water_intake WHERE userId = :userId AND date = :date")
    suspend fun getTodayTotalSync(userId: String, date: String): Float?

    @Delete
    suspend fun deleteIntake(waterIntake: WaterIntake)

    @Query("DELETE FROM water_intake WHERE userId = :userId")
    suspend fun deleteAllUserIntakes(userId: String)
}
