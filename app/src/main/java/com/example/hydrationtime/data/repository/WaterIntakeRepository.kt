package com.example.hydrationtime.data.repository

import androidx.lifecycle.LiveData
import com.example.hydrationtime.data.local.dao.*
import com.example.hydrationtime.data.local.entities.*

/**
 * WaterIntakeRepository - Gestion des consommations d'eau
 */
class WaterIntakeRepository(private val waterIntakeDao: WaterIntakeDao) {

    fun getAllIntakes(userId: String): LiveData<List<WaterIntake>> {
        return waterIntakeDao.getAllIntakes(userId)
    }

    fun getIntakesByDate(userId: String, date: String): LiveData<List<WaterIntake>> {
        return waterIntakeDao.getIntakesByDate(userId, date)
    }

    fun getWeeklyIntakes(userId: String, startDate: String): LiveData<List<DailyIntake>> {
        return waterIntakeDao.getWeeklyIntakes(userId, startDate)
    }

    fun getTodayTotal(userId: String, date: String): LiveData<Float?> {
        return waterIntakeDao.getTodayTotal(userId, date)
    }

    suspend fun addWaterIntake(userId: String, amount: Float, date: String) {
        val intake = WaterIntake(
            userId = userId,
            amount = amount,
            date = date
        )
        waterIntakeDao.insertWaterIntake(intake)
    }

    suspend fun deleteIntake(waterIntake: WaterIntake) {
        waterIntakeDao.deleteIntake(waterIntake)
    }
}
