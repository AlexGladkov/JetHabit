package feature.reminders.domain.schedule

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ScheduleEngineTest {

    private val json = Json { encodeDefaults = true }
    private val berlin = TimeZone.of("Europe/Berlin")
    private val newYork = TimeZone.of("America/New_York")

    // -- schedule_daily_basic ------------------------------------------------

    @Test
    fun schedule_daily_basic_before_slot() {
        val config = daily(anchor = LocalDateTime(2_026, 1, 5, 9, 0))
        val now = Instant.parse("2026-01-05T06:00:00Z")

        val result = ScheduleEngine.nextOccurrence(config, completedAt = null, now = now)

        assertEquals(
            LocalDateTime(2_026, 1, 5, 9, 0).toInstant(berlin),
            assertNotNull(result)
        )
    }

    @Test
    fun schedule_daily_basic_after_completion() {
        val config = daily(anchor = LocalDateTime(2_026, 1, 5, 9, 0))
        val completedAt = LocalDateTime(2_026, 1, 5, 9, 30).toInstant(berlin)
        val now = Instant.parse("2026-01-05T06:00:00Z")

        val result = ScheduleEngine.nextOccurrence(config, completedAt, now)

        assertEquals(
            LocalDateTime(2_026, 1, 6, 9, 0).toInstant(berlin),
            assertNotNull(result)
        )
    }

    // -- schedule_dst_gap_shifts_forward --------------------------------------

    @Test
    fun schedule_dst_gap_shifts_forward() {
        // 02:30 local does not exist on 2025-03-30 in Berlin (spring-forward gap).
        val config = daily(anchor = LocalDateTime(2_025, 3, 30, 2, 30), tzId = "Europe/Berlin")
        val now = Instant.parse("2025-03-29T20:00:00Z")

        val result = ScheduleEngine.nextOccurrence(config, completedAt = null, now = now)

        val local = assertNotNull(result).toLocalDateTime(berlin)
        // Deterministic shift forward past the gap: 02:30 -> 03:30, no exception.
        assertEquals(LocalDateTime(2_025, 3, 30, 3, 30), local)

        // Following days keep the 02:30 anchor time.
        val nextDay = ScheduleEngine.nextOccurrence(config, completedAt = result, now = result)
        assertEquals(LocalDateTime(2_025, 3, 31, 2, 30), assertNotNull(nextDay).toLocalDateTime(berlin))
    }

    // -- schedule_dst_overlap_earlier_offset ----------------------------------

    @Test
    fun schedule_dst_overlap_earlier_offset() {
        // 01:30 local occurs twice on 2025-11-02 in New York (fall-back overlap).
        val config = daily(anchor = LocalDateTime(2_025, 11, 2, 1, 30), tzId = "America/New_York")
        val now = Instant.parse("2025-11-01T20:00:00Z")

        val result = ScheduleEngine.nextOccurrence(config, completedAt = null, now = now)

        // Earlier offset (UTC-4) -> single unambiguous instant 05:30Z.
        assertEquals(Instant.parse("2025-11-02T05:30:00Z"), assertNotNull(result))
    }

    // -- schedule_dst_apia_auckland_boundaries --------------------------------

    @Test
    fun schedule_dst_apia_auckland_boundaries() {
        // Pacific/Apia skipped 2011-12-30 entirely (+24h jump). Daily schedule must not
        // hang, return null or land on a duplicated local time.
        val apia = daily(anchor = LocalDateTime(2_011, 12, 29, 9, 0), tzId = "Pacific/Apia")
        val nowApia = Instant.parse("2011-12-30T20:00:00Z") // local: 2011-12-31 10:00

        val apiaResult = assertNotNull(
            ScheduleEngine.nextOccurrence(apia, completedAt = null, now = nowApia)
        )
        val apiaTz = TimeZone.of("Pacific/Apia")
        val apiaLocal = apiaResult.toLocalDateTime(apiaTz)
        assertTrue(apiaLocal.date >= LocalDate(2_011, 12, 31))
        assertEquals(9, apiaLocal.hour)

        // Auckland 2025-09-28 spring-forward: local anchor time is kept on later days.
        val auckland = daily(anchor = LocalDateTime(2_025, 9, 28, 2, 30), tzId = "Pacific/Auckland")
        val nowAuckland = Instant.parse("2025-09-28T10:00:00Z") // local: 2025-09-28 22:00, before the transition
        val aucklandResult = assertNotNull(
            ScheduleEngine.nextOccurrence(auckland, completedAt = null, now = nowAuckland)
        )
        val aucklandTz = TimeZone.of("Pacific/Auckland")
        assertEquals(
            LocalDateTime(2_025, 9, 29, 2, 30),
            aucklandResult.toLocalDateTime(aucklandTz)
        )
    }

    // -- schedule_interval_days_from_anchor -----------------------------------

    @Test
    fun schedule_interval_days_from_anchor() {
        val config = ReminderConfig(
            anchor = LocalDateTime(2_026, 2, 1, 8, 0),
            frequency = Frequency.Interval(days = 3),
            tzId = "Europe/Berlin"
        )
        val completedAt = LocalDateTime(2_026, 2, 10, 22, 0).toInstant(berlin)
        val now = completedAt

        val result = ScheduleEngine.nextOccurrence(config, completedAt, now)

        // Steps counted from the anchor in local time: 1, 4, 7, 10, 13 -> next is 13th.
        assertEquals(
            LocalDateTime(2_026, 2, 13, 8, 0).toInstant(berlin),
            assertNotNull(result)
        )
    }

    @Test
    fun schedule_interval_floor_between_series_dates_stays_on_anchor_series() {
        val config = ReminderConfig(
            anchor = LocalDateTime(2_026, 2, 1, 8, 0),
            frequency = Frequency.Interval(days = 3),
            tzId = "Europe/Berlin"
        )
        val floor = LocalDateTime(2_026, 2, 11, 7, 0).toInstant(berlin)

        val result = ScheduleEngine.nextOccurrence(config, completedAt = null, now = floor)

        // Series dates are Feb 1, 4, 7, 10, 13; the floor date must not become a new origin.
        assertEquals(
            LocalDateTime(2_026, 2, 13, 8, 0).toInstant(berlin),
            assertNotNull(result)
        )
    }

    // -- schedule_weekly_dayset_selection -------------------------------------

    @Test
    fun schedule_weekly_single_day_floor_on_unselected_weekday_stays_on_dayset() {
        val config = ReminderConfig(
            anchor = LocalDateTime(2_026, 2, 2, 9, 0), // Monday
            frequency = Frequency.Weekly(setOf(DayOfWeek.MONDAY)),
            tzId = "Europe/Berlin"
        )
        val floor = LocalDateTime(2_026, 2, 10, 8, 0).toInstant(berlin) // Tuesday before anchor time

        val result = ScheduleEngine.nextOccurrence(config, completedAt = null, now = floor)

        assertEquals(
            LocalDateTime(2_026, 2, 16, 9, 0).toInstant(berlin),
            assertNotNull(result)
        )
    }

    @Test
    fun schedule_weekly_dayset_selection() {
        val config = ReminderConfig(
            anchor = LocalDateTime(2_026, 1, 5, 9, 0), // Monday
            frequency = Frequency.Weekly(setOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)),
            tzId = "Europe/Berlin"
        )

        // Monday 10:00 local -> Thursday 09:00 local (day-of-week in config TZ).
        val fromMonday = ScheduleEngine.nextOccurrence(
            config,
            completedAt = null,
            now = LocalDateTime(2_026, 1, 5, 10, 0).toInstant(berlin)
        )
        assertEquals(
            LocalDateTime(2_026, 1, 8, 9, 0).toInstant(berlin),
            assertNotNull(fromMonday)
        )

        // Thursday evening -> next Monday 09:00 local.
        val fromThursday = ScheduleEngine.nextOccurrence(
            config,
            completedAt = null,
            now = LocalDateTime(2_026, 1, 8, 20, 0).toInstant(berlin)
        )
        assertEquals(
            LocalDateTime(2_026, 1, 12, 9, 0).toInstant(berlin),
            assertNotNull(fromThursday)
        )
    }

    @Test
    fun schedule_weekly_tz_local() {
        // Same instant is Monday 22:00 in Berlin but Monday 16:00 in New York:
        // day-of-week must be resolved in the config TZ (both Monday here, but the
        // slot 09:00 Monday NY is still ahead only when computed in New York time).
        val config = ReminderConfig(
            anchor = LocalDateTime(2_026, 1, 5, 9, 0),
            frequency = Frequency.Weekly(setOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)),
            tzId = "America/New_York"
        )
        val now = LocalDateTime(2_026, 1, 5, 10, 0).toInstant(berlin) // 04:00 NY, Monday

        val result = ScheduleEngine.nextOccurrence(config, completedAt = null, now = now)

        assertEquals(
            LocalDateTime(2_026, 1, 5, 9, 0).toInstant(newYork),
            assertNotNull(result)
        )
    }

    // -- schedule_invariant_strictly_after_max --------------------------------

    @Test
    fun schedule_invariant_strictly_after_max() {
        val now = Instant.parse("2025-06-15T10:00:00Z")
        val configs = listOf(
            daily(LocalDateTime(2_025, 6, 10, 9, 0)),
            ReminderConfig(
                anchor = LocalDateTime(2_025, 6, 10, 9, 0),
                frequency = Frequency.Weekly(setOf(DayOfWeek.TUESDAY, DayOfWeek.SATURDAY)),
                tzId = "Europe/Berlin"
            ),
            ReminderConfig(
                anchor = LocalDateTime(2_025, 6, 10, 9, 0),
                frequency = Frequency.Interval(days = 5),
                tzId = "America/New_York"
            )
        )

        configs.forEach { config ->
            val completedAt = Instant.parse("2025-06-14T10:00:00Z")
            val result = ScheduleEngine.nextOccurrence(config, completedAt, now)
            assertNotNull(result)
            assertTrue(result > maxOf(completedAt, now))

            // Monotonic: feeding the result back strictly advances (no scheduler loop).
            val advanced = ScheduleEngine.nextOccurrence(config, completedAt = result, now = result)
            assertTrue(assertNotNull(advanced) > result)
        }
    }

    // -- schedule_end_date_returns_null ----------------------------------------

    @Test
    fun schedule_end_date_returns_null() {
        val expired = daily(
            anchor = LocalDateTime(2_025, 1, 1, 9, 0),
            endDate = LocalDate(2_025, 6, 1)
        )
        val now = Instant.parse("2025-06-15T10:00:00Z")

        assertEquals(null, ScheduleEngine.nextOccurrence(expired, completedAt = null, now = now))

        // endDate = null -> never null because of the end.
        val open = daily(anchor = LocalDateTime(2_025, 1, 1, 9, 0), endDate = null)
        assertNotNull(ScheduleEngine.nextOccurrence(open, completedAt = null, now = now))
    }

    // -- schedule_validation_failfast ------------------------------------------

    @Test
    fun schedule_validation_failfast() {
        assertFailsWith<IllegalArgumentException> {
            ReminderConfig(
                anchor = LocalDateTime(2_026, 1, 5, 9, 0),
                frequency = Frequency.Weekly(emptySet()),
                tzId = "Europe/Berlin"
            )
        }
        assertFailsWith<IllegalArgumentException> {
            ReminderConfig(
                anchor = LocalDateTime(2_026, 1, 5, 9, 0),
                frequency = Frequency.Interval(days = 0),
                tzId = "Europe/Berlin"
            )
        }
        assertFailsWith<IllegalArgumentException> {
            ReminderConfig(
                anchor = LocalDateTime(2_026, 1, 5, 9, 0),
                frequency = Frequency.Interval(days = -1),
                tzId = "Europe/Berlin"
            )
        }
        assertFailsWith<IllegalArgumentException> {
            ReminderConfig(
                anchor = LocalDateTime(2_026, 1, 5, 9, 0),
                frequency = Frequency.Daily,
                tzId = "Not/AZone"
            )
        }
    }

    // -- schedule_serialization_roundtrip ---------------------------------------

    @Test
    fun schedule_serialization_roundtrip() {
        val configs = listOf(
            daily(LocalDateTime(2_026, 1, 5, 9, 0)),
            ReminderConfig(
                anchor = LocalDateTime(2_026, 1, 5, 9, 0),
                frequency = Frequency.Weekly(setOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)),
                tzId = "Europe/Berlin",
                endDate = LocalDate(2_026, 6, 1)
            ),
            ReminderConfig(
                anchor = LocalDateTime(2_026, 1, 5, 9, 0),
                frequency = Frequency.Interval(days = 3),
                tzId = "America/New_York"
            )
        )

        configs.forEach { config ->
            val decoded = json.decodeFromString<ReminderConfig>(json.encodeToString(config))
            assertEquals(config, decoded)

            val element = json.parseToJsonElement(json.encodeToString(config)).jsonObject
            // Stable field names and the "type" discriminator.
            assertContains(element.keys, "anchor")
            assertContains(element.keys, "frequency")
            assertContains(element.keys, "tzId")
            assertContains(element.keys, "endDate")
            assertContains(element["frequency"]!!.jsonObject.keys, "type")
        }

        // Unknown discriminator -> SerializationException.
        val unknown = """
            {"anchor":"2026-01-05T09:00:00","frequency":{"type":"hourly"},"tzId":"Europe/Berlin"}
        """.trimIndent()
        assertFailsWith<SerializationException> {
            json.decodeFromString<ReminderConfig>(unknown)
        }
    }

    // -- helpers ----------------------------------------------------------------

    private fun daily(anchor: LocalDateTime, tzId: String = "Europe/Berlin", endDate: LocalDate? = null) =
        ReminderConfig(
            anchor = anchor,
            frequency = Frequency.Daily,
            tzId = tzId,
            endDate = endDate
        )
}
