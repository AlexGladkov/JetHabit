package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.until

fun LocalDate.daysInMonth(): Int {
    val start = LocalDate(year, month, 1)
    val end = start.plus(1, DateTimeUnit.MONTH)
    return start.until(end, DateTimeUnit.DAY)
}

fun LocalDate.tenDaysPassed(): Boolean {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    return this.daysUntil(today) >= 10
}