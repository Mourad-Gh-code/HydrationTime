package com.example.hydrationtime.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité WaterIntake - Enregistre chaque consommation d'eau
 */
@Entity(tableName = "water_intake")
data class WaterIntake(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val amount: Float, // En litres
    val timestamp: Long = System.currentTimeMillis(),
    val date: String // Format: "yyyy-MM-dd" pour faciliter les requêtes
)
