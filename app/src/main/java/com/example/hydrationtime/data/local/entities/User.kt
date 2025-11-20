
package com.example.hydrationtime.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Entité User - Stocke les informations de l'utilisateur
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val uid: String,
    val name: String,
    val email: String,
    val birthday: Long,
    val dailyGoal: Float = 2.0f, // Objectif en litres
    val createdAt: Long = System.currentTimeMillis()
)