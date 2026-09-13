package feature.reminders.domain.schedule

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
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

        var candidateDate = firstCandidateDate(config, floor, tz)
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

    /** First local date (config TZ) worth checking: never earlier than anchor or the floor date. */
    private fun firstCandidateDate(
        config: ReminderConfig,
        floor: Instant,
        tz: TimeZone
    ): LocalDate {
        val floorLocalDate = floor.toLocalDateTime(tz).date
        return if (floorLocalDate > config.anchor.date) floorLocalDate else config.anchor.date
    }

    /** Next local date for the frequency, strictly after [date]; `null` when it cannot advance. */
    private fun nextDate(frequency: Frequency, date: LocalDate): LocalDate? = when (frequency) {
        is Frequency.Daily -> date.plus(DatePeriod(days = 1))
        is Frequency.Weekly -> nextWeekday(frequency.days, date)
        is Frequency.Interval -> date.plus(DatePeriod(days = frequency.days))
    }

    private fun nextWeekday(days: Set<DayOfWeek>, date: LocalDate): LocalDate? {
        var candidate = date.plus(1, DateTimeUnit.DAY)
        repeat(WEEK_LENGTH) {
            if (candidate.dayOfWeek in days) return candidate
            candidate = candidate.plus(1, DateTimeUnit.DAY)
        }
        return null
    }

    private const val WEEK_LENGTH = 7
}
