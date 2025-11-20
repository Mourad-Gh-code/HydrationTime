package com.example.hydrationtime.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.hydrationtime.data.local.dao.*
import com.example.hydrationtime.data.local.entities.*

/**
 * Base de données Room principale
 */
@Database(
    entities = [
        User::class,
        WaterIntake::class,
        Goal::class,
        DrinkType::class,
        ConsumptionLog::class,
        UserPreferences::class,
        DailyStreak::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun goalDao(): GoalDao
    abstract fun drinkTypeDao(): DrinkTypeDao
    abstract fun consumptionLogDao(): ConsumptionLogDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    abstract fun dailyStreakDao(): DailyStreakDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hydration_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}