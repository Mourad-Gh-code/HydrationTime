package com.example.hydrationtime.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * DateUtils - Utilitaires pour la gestion des dates
 */
object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun getTodayDate(): String {
        return dateFormat.format(Date())
    }

    fun getDateString(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun getDisplayDate(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString)
            displayDateFormat.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    fun getWeekStartDate(daysAgo: Int = 6): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return dateFormat.format(calendar.time)
    }

    fun calculateAge(birthdayTimestamp: Long): Int {
        val birthday = Calendar.getInstance()
        birthday.timeInMillis = birthdayTimestamp

        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthday.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    fun isValidAge(birthdayTimestamp: Long, minAge: Int = Constants.MIN_AGE): Boolean {
        return calculateAge(birthdayTimestamp) >= minAge
    }
}
