package utils

import androidx.compose.ui.text.capitalize
import kotlinx.datetime.*

fun LocalDate.daysShift(days: Int): LocalDate = when {
    days < 0 -> {
        minus(DateTimeUnit.DayBased(-days))
    }
    days > 0 -> {
        plus(DateTimeUnit.DayBased(days))
    }
    else -> this
}

fun LocalDate.daysInAMonth(): Int {
    val end = this.plus(1, DateTimeUnit.MONTH)
    return this.until(end, DateTimeUnit.DAY)
}

fun LocalDateTime.shrinkMonthName(): String = month.name.take(3).lowercase().capitalize()