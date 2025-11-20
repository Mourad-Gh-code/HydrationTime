package com.example.hydrationtime.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ConsumptionLog - Enhanced water intake tracking with drink types
 */
@Entity(tableName = "consumption_logs")
data class ConsumptionLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val drinkTypeId: Int, // Reference to DrinkType
    val drinkName: String, // Cached name for quick access
    val amount: Float, // In ml
    val timestamp: Long = System.currentTimeMillis(),
    val date: String, // Format: "yyyy-MM-dd"
    val color: String = "#29B6F6" // Cached color
)
