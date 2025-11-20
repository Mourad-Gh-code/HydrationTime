package com.example.hydrationtime.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * UserPreferences - Stores user settings
 */
@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey
    val userId: String,
    val isDarkMode: Boolean = false,
    val language: String = "en", // "en", "fr", "ar"
    val dailyGoalMl: Float = 2000f,
    val notificationsEnabled: Boolean = true,
    val notificationIntervalMinutes: Int = 60,
    val startTime: String = "08:00", // Notification start time
    val endTime: String = "22:00", // Notification end time
    val weekStartDay: Int = 1 // 1 = Monday, 7 = Sunday
)
