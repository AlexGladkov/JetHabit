package feature.reminders.domain.schedule

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * Pure, platform-independent schedule engine: computes the next firing instant of a
 * [ReminderConfig]. No Clock, no TimeSource, no platform APIs — `now` is always injected.
 */
object ScheduleEngine {

    /** Hard horizon so a non-advancing (looping) scan terminates with `null` instead of hanging. */
    private const val MAX_CANDIDATES = 20_000

    /**
     * Next occurrence strictly greater than `max(completedAt, now)`, or `null` when the
     * schedule is expired ([ReminderConfig.endDate] passed) or no occurrence is found
     * within the scan horizon. Pure and idempotent.
     */
    fun nextOccurrence(config: ReminderConfig, completedAt: Instant?, now: Instant): Instant? {
        val tz = TimeZone.of(config.tzId)
        val floor = maxOf(completedAt ?: Instant.DISTANT_PAST, now)

        var candidateDate = firstCandidateDate(config, floor, tz) ?: return null
        repeat(MAX_CANDIDATES) {
            if (config.endDate != null && candidateDate > config.endDate) return null

            val candidate = LocalDateTime(candidateDate, config.anchor.time)
            val instant = candidate.toInstant(tz) // gap -> shifts forward; overlap -> earlier offset
            if (instant > floor) return instant

            val next = nextDate(config.frequency, candidateDate) ?: return null
            if (next <= candidateDate) return null // non-advancing guard: never loop forever
            candidateDate = next
        }
        return null
    }

    /** First local date (config TZ) worth checking, aligned to the recurrence series. */
    private fun firstCandidateDate(
        config: ReminderConfig,
        floor: Instant,
        tz: TimeZone
    ): LocalDate? {
        val floorLocalDate = floor.toLocalDateTime(tz).date
        val firstPossibleDate = maxOf(config.anchor.date, floorLocalDate)
        return when (val frequency = config.frequency) {
            is Frequency.Daily -> firstPossibleDate
            is Frequency.Weekly -> {
                if (firstPossibleDate.dayOfWeek in frequency.days) firstPossibleDate
                else nextWeekday(frequency.days, firstPossibleDate)
            }
            is Frequency.Interval -> {
                val daysFromAnchor = config.anchor.date.daysUntil(firstPossibleDate)
                val daysToNextOccurrence =
                    (frequency.days - daysFromAnchor % frequency.days) % frequency.days
                safePlusDays(firstPossibleDate, daysToNextOccurrence)
            }
        }
    }

    /** Next local date for the frequency, strictly after [date]; `null` when it cannot advance. */
    private fun nextDate(frequency: Frequency, date: LocalDate): LocalDate? = when (frequency) {
        is Frequency.Daily -> safePlusDays(date, 1)
        is Frequency.Weekly -> nextWeekday(frequency.days, date)
        is Frequency.Interval -> safePlusDays(date, frequency.days)
    }

    private fun nextWeekday(days: Set<DayOfWeek>, date: LocalDate): LocalDate? {
        var candidate = date
        repeat(WEEK_LENGTH) {
            candidate = safePlusDays(candidate, 1) ?: return null
            if (candidate.dayOfWeek in days) return candidate
        }
        return null
    }

    private fun safePlusDays(date: LocalDate, days: Int): LocalDate? =
        runCatching { date.plus(days, DateTimeUnit.DAY) }.getOrNull()

    private const val WEEK_LENGTH = 7
}
