package com.example.hydrationtime.ui.fragments.goals

import android.app.Application

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.hydrationtime.data.local.database.AppDatabase
import com.example.hydrationtime.data.local.entities.Goal

import com.example.hydrationtime.data.repository.GoalRepository

import com.example.hydrationtime.utils.DateUtils

import kotlinx.coroutines.launch

/**
 * GoalsViewModel - ViewModel pour les objectifs
 */
class GoalsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val goalRepository = GoalRepository(
        database.goalDao(),
        database.userDao(),
        database.waterIntakeDao()
    )

    private val _currentProgress = MediatorLiveData<Float>()
    val currentProgress: LiveData<Float> = _currentProgress

    private val _goalsHistory = MediatorLiveData<List<Goal>>()
    val goalsHistory: LiveData<List<Goal>> = _goalsHistory

    private val _dailyGoal = MediatorLiveData<Float>()
    val dailyGoal: LiveData<Float> = _dailyGoal

    private val _todayConsumed = MediatorLiveData<Float>()
    val todayConsumed: LiveData<Float> = _todayConsumed

    fun loadGoals(userId: String) {
        val today = DateUtils.getTodayDate()

        // Charger l'historique des objectifs
        val goalsSource = goalRepository.getAllGoals(userId)
        _goalsHistory.addSource(goalsSource) { goals ->
            _goalsHistory.value = goals
        }

        // Charger l'objectif quotidien actuel
        val userSource = database.userDao().getUserById(userId)
        _dailyGoal.addSource(userSource) { user ->
            _dailyGoal.value = user?.dailyGoal ?: 2.0f
        }

        // Charger la consommation du jour
        val todaySource = database.waterIntakeDao().getTodayTotal(userId, today)
        _todayConsumed.addSource(todaySource) { consumed ->
            _todayConsumed.value = consumed ?: 0f
            calculateProgress()
        }

        // Recalculer la progression quand l'objectif change
        _dailyGoal.observeForever { calculateProgress() }
    }

    private fun calculateProgress() {
        val consumed = _todayConsumed.value ?: 0f
        val goal = _dailyGoal.value ?: 2.0f
        _currentProgress.value = if (goal > 0) (consumed / goal).coerceIn(0f, 1f) else 0f
    }

    fun setDailyGoal(userId: String, newGoal: Float) {
        viewModelScope.launch {
            val today = DateUtils.getTodayDate()
            goalRepository.createOrUpdateDailyGoal(userId, newGoal, today)
        }
    }
}