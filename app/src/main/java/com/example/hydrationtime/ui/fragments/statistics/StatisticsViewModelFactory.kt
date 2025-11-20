package com.example.hydrationtime.ui.fragments.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hydrationtime.data.local.dao.ConsumptionLogDao

class StatisticsViewModelFactory(
    private val consumptionLogDao: ConsumptionLogDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            return StatisticsViewModel(consumptionLogDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
