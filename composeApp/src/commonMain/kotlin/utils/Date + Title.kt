package utils

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import kotlinx.datetime.*
import tech.mobiledeveloper.jethabit.app.AppRes

fun LocalDate.getTitle(): String {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val isToday = this.dayOfYear == today.dayOfYear && this.year == today.year
    val isYesterday = this.daysUntil(today) == 1
    
    if (isToday) return AppRes.string.days_today
    if (isYesterday) return AppRes.string.days_yesterday
    val monthName = month.name.substring(0, 3)
        .toLowerCase(Locale.current)
        .capitalize(Locale.current)
    
    return "$dayOfMonth $monthName"
}