package feature.reminders.domain.schedule

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** How often a reminder repeats. Sealed: extend with new subtypes + a `when` branch in ScheduleEngine. */
@Serializable
sealed class Frequency {

    /** Every day at the anchor time-of-day. */
    @Serializable
    @SerialName("daily")
    data object Daily : Frequency()

    /** On the given set of weekdays at the anchor time-of-day. */
    @Serializable
    @SerialName("weekly")
    data class Weekly(val days: Set<DayOfWeek>) : Frequency()

    /** Every [days] days from the anchor date, at the anchor time-of-day. */
    @Serializable
    @SerialName("interval")
    data class Interval(val days: Int) : Frequency()
}

@Serializable
data class ReminderConfig(
    /** Base date+time (local wall time in [tzId]) from which occurrences are counted. */
    val anchor: LocalDateTime,
    val frequency: Frequency,
    /** IANA time-zone id persisted as String — never a TimeZone object. */
    val tzId: String,
    /** Local end date (inclusive, config TZ); beyond it the schedule is expired. */
    val endDate: LocalDate? = null
) {
    init {
        validate(anchor, frequency, tzId)
    }

    companion object {
        /**
         * Fail-fast boundary validation: throws [IllegalArgumentException] on an empty
         * weekly day-set, non-positive interval or unknown IANA tz id.
         */
        fun validate(anchor: LocalDateTime, frequency: Frequency, tzId: String) {
            require(anchor.year in MIN_YEAR..MAX_YEAR) { "anchor year out of supported range: ${anchor.year}" }
            when (frequency) {
                is Frequency.Daily -> Unit
                is Frequency.Weekly -> require(frequency.days.isNotEmpty()) { "Weekly frequency must have at least one day" }
                is Frequency.Interval -> require(frequency.days > 0) { "Interval days must be positive, was ${frequency.days}" }
            }
            require(tzId.isNotBlank()) { "tzId must not be blank" }
            // TimeZone.of throws IllegalArgumentException for unknown IANA ids.
            TimeZone.of(tzId)
        }

        private const val MIN_YEAR = 1
        private const val MAX_YEAR = 9999
    }
}
