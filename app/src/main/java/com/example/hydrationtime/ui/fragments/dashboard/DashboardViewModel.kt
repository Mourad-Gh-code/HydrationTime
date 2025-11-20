package com.example.hydrationtime.ui.fragments.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.example.hydrationtime.data.local.database.AppDatabase
import com.example.hydrationtime.data.local.entities.DailyIntake
import com.example.hydrationtime.data.repository.GoalRepository
import com.example.hydrationtime.data.repository.WaterIntakeRepository
import com.example.hydrationtime.utils.DateUtils
import kotlinx.coroutines.launch


class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val waterIntakeRepository = WaterIntakeRepository(database.waterIntakeDao())
    private val goalRepository = GoalRepository(
        database.goalDao(),
        database.userDao(),
        database.waterIntakeDao()
    )

    private val _todayTotal = MediatorLiveData<Float>()
    val todayTotal: LiveData<Float> = _todayTotal

    private val _weeklyData = MediatorLiveData<List<DailyIntake>>()
    val weeklyData: LiveData<List<DailyIntake>> = _weeklyData

    private val _userDailyGoal = MediatorLiveData<Float>()
    val userDailyGoal: LiveData<Float> = _userDailyGoal

    fun loadDashboardData(userId: String) {
        val today = DateUtils.getTodayDate()
        val weekStart = DateUtils.getWeekStartDate()

        // Charger la consommation du jour
        val todaySource = waterIntakeRepository.getTodayTotal(userId, today)
        _todayTotal.addSource(todaySource) { value ->
            _todayTotal.value = value ?: 0f
        }

        // Charger les données de la semaine
        val weeklySource = waterIntakeRepository.getWeeklyIntakes(userId, weekStart)
        _weeklyData.addSource(weeklySource) { data ->
            _weeklyData.value = data
        }

        // Charger l'objectif quotidien
        val userSource = database.userDao().getUserById(userId)
        _userDailyGoal.addSource(userSource) { user ->
            _userDailyGoal.value = user?.dailyGoal ?: 2.0f
        }
    }

    fun addWaterIntake(userId: String, amount: Float) {
        viewModelScope.launch {
            val today = DateUtils.getTodayDate()
            waterIntakeRepository.addWaterIntake(userId, amount, today)

            // Mettre à jour la progression de l'objectif
            goalRepository.updateGoalProgress(userId, today)
        }
    }
}