package com.example.hydrationtime.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.hydrationtime.data.local.entities.ConsumptionLog

@Dao
interface ConsumptionLogDao {
    @Query("SELECT * FROM consumption_logs WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    fun getTodayLogs(userId: String, date: String): LiveData<List<ConsumptionLog>>

    @Query("SELECT SUM(amount) FROM consumption_logs WHERE userId = :userId AND date = :date")
    fun getTodayTotal(userId: String, date: String): LiveData<Float?>

    @Query("SELECT * FROM consumption_logs WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    suspend fun getLogsInRange(userId: String, startDate: String, endDate: String): List<ConsumptionLog>

    @Query("SELECT * FROM consumption_logs WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    suspend fun getLogsByDate(userId: String, date: String): List<ConsumptionLog>

    @Query("SELECT SUM(amount) FROM consumption_logs WHERE userId = :userId AND date = :date")
    suspend fun getTotalByDate(userId: String, date: String): Float?

    @Query("SELECT DISTINCT date FROM consumption_logs WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    suspend fun getLoggedDates(userId: String, startDate: String, endDate: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: ConsumptionLog): Long

    @Delete
    suspend fun deleteLog(log: ConsumptionLog)

    @Query("DELETE FROM consumption_logs WHERE userId = :userId AND date = :date")
    suspend fun deleteLogsByDate(userId: String, date: String)

    @Query("DELETE FROM consumption_logs WHERE userId = :userId")
    suspend fun deleteAllLogs(userId: String)

    // Statistics queries
    @Query("""
        SELECT strftime('%H', datetime(timestamp/1000, 'unixepoch')) as hour,
               drinkName,
               SUM(amount) as amount
        FROM consumption_logs
        WHERE userId = :userId AND date = :date
        GROUP BY hour, drinkName
        ORDER BY hour ASC
    """)
    suspend fun getHourlyConsumption(userId: String, date: String): List<HourlyConsumption>

    @Query("""
        SELECT date,
               drinkName,
               SUM(amount) as amount
        FROM consumption_logs
        WHERE userId = :userId AND date BETWEEN :startDate AND :endDate
        GROUP BY date, drinkName
        ORDER BY date ASC
    """)
    suspend fun getDailyConsumptionInRange(userId: String, startDate: String, endDate: String): List<DailyConsumptionByType>

    @Query("""
        SELECT drinkName,
               SUM(amount) as amount
        FROM consumption_logs
        WHERE userId = :userId AND date BETWEEN :startDate AND :endDate
        GROUP BY drinkName
    """)
    suspend fun getDrinkTypeDistribution(userId: String, startDate: String, endDate: String): List<DrinkTypeAmount>
}

data class HourlyConsumption(
    val hour: String,
    val drinkName: String,
    val amount: Float
)

data class DailyConsumptionByType(
    val date: String,
    val drinkName: String,
    val amount: Float
)

data class DrinkTypeAmount(
    val drinkName: String,
    val amount: Float
)
