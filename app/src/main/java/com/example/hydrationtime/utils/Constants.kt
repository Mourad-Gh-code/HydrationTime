package com.example.hydrationtime.utils

object Constants {
    // Shared Preferences
    const val PREFS_NAME = "hydration_time_prefs"
    const val KEY_FIRST_TIME = "is_first_time"
    const val KEY_NOTIFICATION_ENABLED = "notification_enabled"

    // Notifications
    const val NOTIFICATION_CHANNEL_ID = "water_reminder_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Rappels d'hydratation"
    const val NOTIFICATION_ID = 1001

    // Theme Mode
    const val KEY_THEME_MODE = "theme_mode"
    const val THEME_LIGHT = 1
    const val THEME_DARK = 2

    // Language Selection
    const val KEY_LANGUAGE = "app_language"
    const val LANG_EN = "en"
    const val LANG_FR = "fr"
    const val LANG_AR = "ar"

    // Other
    const val MIN_AGE = 18
    const val DEFAULT_GOAL = 2.0f // Litres
}