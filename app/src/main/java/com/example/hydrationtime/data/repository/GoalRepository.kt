package com.example.hydrationtime.data.repository

import androidx.lifecycle.LiveData
import com.example.hydrationtime.data.local.dao.*
import com.example.hydrationtime.data.local.entities.*


/**
 * GoalRepository - Gestion des objectifs
 */
class GoalRepository(
    private val goalDao: GoalDao,
    private val userDao: UserDao,
    private val waterIntakeDao: WaterIntakeDao
) {

    fun getAllGoals(userId: String): LiveData<List<Goal>> {
        return goalDao.getAllGoals(userId)
    }

    suspend fun createOrUpdateDailyGoal(userId: String, targetAmount: Float, date: String) {
        val existingGoal = goalDao.getGoalByDate(userId, date)
        val achievedAmount = waterIntakeDao.getTodayTotalSync(userId, date) ?: 0f
        val achieved = achievedAmount >= targetAmount

        if (existingGoal != null) {
            val updatedGoal = existingGoal.copy(
                targetAmount = targetAmount,
                achievedAmount = achievedAmount,
                achieved = achieved
            )
            goalDao.updateGoal(updatedGoal)
        } else {
            val newGoal = Goal(
                userId = userId,
                targetAmount = targetAmount,
                achievedAmount = achievedAmount,
                date = date,
                achieved = achieved
            )
            goalDao.insertGoal(newGoal)
        }

        // Mettre à jour l'objectif quotidien de l'utilisateur
        val user = userDao.getUserByIdSync(userId)
        user?.let {
            val updatedUser = it.copy(dailyGoal = targetAmount)
            userDao.updateUser(updatedUser)
        }
    }

    suspend fun updateGoalProgress(userId: String, date: String) {
        val goal = goalDao.getGoalByDate(userId, date)
        goal?.let {
            val achievedAmount = waterIntakeDao.getTodayTotalSync(userId, date) ?: 0f
            val updatedGoal = it.copy(
                achievedAmount = achievedAmount,
                achieved = achievedAmount >= it.targetAmount
            )
            goalDao.updateGoal(updatedGoal)
        }
    }
}