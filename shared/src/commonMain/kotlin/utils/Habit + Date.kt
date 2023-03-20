package utils

import data.features.habit.models.Habit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

fun Habit.isInDates(currentDate: LocalDateTime): Boolean {
    val startDate = startDate ?: return true
    val endDate = endDate ?: return true

    val epochCur = currentDate.toInstant(TimeZone.currentSystemDefault()).epochSeconds
    val epochStart = startDate.epochSeconds
    val epochEnd = endDate.epochSeconds

    return epochCur in epochStart..epochEnd
}