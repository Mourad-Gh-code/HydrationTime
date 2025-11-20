package com.example.hydrationtime.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DailyStreak - Tracks daily goal achievements (Duolingo-style)
 */
@Entity(tableName = "daily_streaks")
data class DailyStreak(
    @PrimaryKey
    val date: String, // Format: "yyyy-MM-dd"
    val userId: String,
    val goalMl: Float,
    val consumedMl: Float,
    val goalAchieved: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
