package com.example.hydrationtime.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DrinkType - Represents different types of beverages
 */
@Entity(tableName = "drink_types")
data class DrinkType(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String,
    val name: String, // e.g., "Water", "Tea", "Coffee", "Orange Juice"
    val color: String, // Hex color code
    val iconName: String = "ic_water", // Icon resource name
    val defaultAmount: Float = 250f, // Default amount in ml
    val isCustom: Boolean = false // User-created or predefined
)

/**
 * Predefined drink types
 */
object PredefinedDrinks {
    fun getDefaults(userId: String) = listOf(
        DrinkType(0, userId, "Water", "#29B6F6", "ic_water", 250f, false),
        DrinkType(0, userId, "Tea", "#66BB6A", "ic_tea", 200f, false),
        DrinkType(0, userId, "Coffee", "#8D6E63", "ic_coffee", 150f, false),
        DrinkType(0, userId, "Orange Juice", "#FF9800", "ic_juice", 200f, false),
        DrinkType(0, userId, "Smoothie", "#E91E63", "ic_smoothie", 300f, false),
        DrinkType(0, userId, "Milk", "#FFFFFF", "ic_milk", 250f, false)
    )
}
