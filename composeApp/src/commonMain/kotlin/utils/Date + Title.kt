package utils

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import kotlinx.datetime.*

fun LocalDate.getTitle(): Weekday {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val isToday = this.dayOfYear == today.dayOfYear && this.year == today.year
    val isYesterday = this.daysUntil(today) == 1
    
    if (isToday) return Weekday.Today
    if (isYesterday) return Weekday.Yesterday
    val monthName = month.name.substring(0, 3)
        .toLowerCase(Locale.current)
        .capitalize(Locale.current)
    
    return Weekday.Custom("$dayOfMonth $monthName")
}