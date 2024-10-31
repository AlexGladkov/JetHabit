package utils

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import kotlinx.datetime.until

private const val TEN = 10

fun LocalDate.daysInMonth(): Int {
    val start = LocalDate(year, month, 1)
    val end = start.plus(1, DateTimeUnit.MONTH)
    return start.until(end, DateTimeUnit.DAY)
}

fun tenDaysPassed(startDate: LocalDate) : Boolean {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
    val daysBetween = startDate.daysUntil(today)
    return daysBetween >= TEN
}