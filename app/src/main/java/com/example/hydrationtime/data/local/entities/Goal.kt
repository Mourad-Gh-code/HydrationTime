package com.example.hydrationtime.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val targetAmount: Float, // Objectif en litres
    val achievedAmount: Float, // Quantité atteinte
    val date: String, // Format: "yyyy-MM-dd"
    val achieved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Data class pour les statistiques du dashboard
 */
