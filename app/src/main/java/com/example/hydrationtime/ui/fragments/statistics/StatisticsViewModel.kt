package com.example.hydrationtime.ui.fragments.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hydrationtime.data.local.dao.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatisticsViewModel(
    private val consumptionLogDao: ConsumptionLogDao
) : ViewModel() {

    private val userId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private val _totalConsumption = MutableLiveData<Float>()
    val totalConsumption: LiveData<Float> = _totalConsumption

    private val _avgDaily = MutableLiveData<Float>()
    val avgDaily: LiveData<Float> = _avgDaily

    private val _mostLoggedDrink = MutableLiveData<String>()
    val mostLoggedDrink: LiveData<String> = _mostLoggedDrink

    private val _goalAchievement = MutableLiveData<Int>()
    val goalAchievement: LiveData<Int> = _goalAchievement

    private val _drinkDistribution = MutableLiveData<Map<String, Float>>()
    val drinkDistribution: LiveData<Map<String, Float>> = _drinkDistribution

    private val _chartData = MutableLiveData<List<ChartEntry>>()
    val chartData: LiveData<List<ChartEntry>> = _chartData

    private val _hourlyData = MutableLiveData<List<HourlyConsumption>>()
    val hourlyData: LiveData<List<HourlyConsumption>> = _hourlyData

    private val _dailyDataInRange = MutableLiveData<List<DailyConsumptionByType>>()
    val dailyDataInRange: LiveData<List<DailyConsumptionByType>> = _dailyDataInRange

    fun loadDataForPeriod(period: StatisticsFragment.Period) {
        viewModelScope.launch {
            when (period) {
                StatisticsFragment.Period.DAILY -> loadDailyData()
                StatisticsFragment.Period.WEEKLY -> loadWeeklyData()
                StatisticsFragment.Period.MONTHLY -> loadMonthlyData()
                StatisticsFragment.Period.YEARLY -> loadYearlyData()
            }
        }
    }

    private suspend fun loadDailyData() {
        val today = getTodayDate()

        // Load hourly consumption
        val hourlyData = consumptionLogDao.getHourlyConsumption(userId, today)
        _hourlyData.value = hourlyData

        // Load statistics for today
        loadStatistics(today, today)
    }

    private suspend fun loadWeeklyData() {
        val today = getTodayDate()
        val weekAgo = getDateDaysAgo(6)

        // Load daily consumption for the week
        val dailyData = consumptionLogDao.getDailyConsumptionInRange(userId, weekAgo, today)
        _dailyDataInRange.value = dailyData

        // Load statistics for the week
        loadStatistics(weekAgo, today)
    }

    private suspend fun loadMonthlyData() {
        val today = getTodayDate()
        val monthAgo = getDateDaysAgo(29)

        // Load daily consumption for the month
        val dailyData = consumptionLogDao.getDailyConsumptionInRange(userId, monthAgo, today)
        _dailyDataInRange.value = dailyData

        // Load statistics for the month
        loadStatistics(monthAgo, today)
    }

    private suspend fun loadYearlyData() {
        val today = getTodayDate()
        val yearAgo = getDateDaysAgo(364)

        // Load monthly consumption for the year
        val dailyData = consumptionLogDao.getDailyConsumptionInRange(userId, yearAgo, today)
        _dailyDataInRange.value = dailyData

        // Load statistics for the year
        loadStatistics(yearAgo, today)
    }

    private suspend fun loadStatistics(startDate: String, endDate: String) {
        // Load drink distribution
        val distribution = consumptionLogDao.getDrinkTypeDistribution(userId, startDate, endDate)
        val distMap = distribution.associate { it.drinkName to it.amount / 1000f } // Convert to liters
        _drinkDistribution.value = distMap

        // Calculate total
        val total = distMap.values.sum()
        _totalConsumption.value = total

        // Calculate average daily
        val loggedDates = consumptionLogDao.getLoggedDates(userId, startDate, endDate)
        val avgDaily = if (loggedDates.isNotEmpty()) total / loggedDates.size else 0f
        _avgDaily.value = avgDaily

        // Most logged drink
        val mostLogged = distMap.maxByOrNull { it.value }?.key ?: "None"
        _mostLoggedDrink.value = mostLogged

        // Goal achievement (days with consumption)
        _goalAchievement.value = loggedDates.size
    }

    data class ChartEntry(
        val label: String,
        val value: Float,
        val drinkBreakdown: Map<String, Float>
    )

    companion object {
        fun getTodayDate(): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(Date())
        }

        fun getDateDaysAgo(daysAgo: Int): String {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return sdf.format(calendar.time)
        }
    }
}
