package feature.reminders.schedule

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import feature.reminders.domain.schedule.ReminderScheduleResult
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Regression repro: a transient 'notifications off' must NOT delete the durable reminder record.
 * Fails before the fix (record is deleted); passes after the fix (record retained).
 */
@RunWith(AndroidJUnit4::class)
class PermissionDeniedDurabilityReproAndroidTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefs = context.getSharedPreferences(AndroidReminderScheduler.PREFS, Context.MODE_PRIVATE)
    private val now = 1_700_000_000_000L
    private val at = now + 60_000
    private val id = "repro.denied.durability"

    @After
    fun clean() {
        prefs.edit().clear().commit()
    }

    @Test
    fun transientPermissionDeniedKeepsDurableRecord() = runBlocking {
        // 1. Schedule while permission granted -> durable record exists.
        val allowed = AndroidReminderScheduler(context, AlarmInstaller { }, { true }, { now })
        assertEquals(ReminderScheduleResult.Scheduled, allowed.schedule(id, Instant.fromEpochMilliseconds(at)))
        assertTrue(prefs.contains(AndroidReminderScheduler.storageKey(id)))
        assertEquals(at, prefs.getLong(AndroidReminderScheduler.storageKey(id), -1))

        // 2. Same reminder, transient POST_NOTIFICATIONS denial (e.g. user toggled notifications off).
        val denied = AndroidReminderScheduler(context, AlarmInstaller { }, { false }, { now })
        assertEquals(ReminderScheduleResult.PermissionDenied, denied.schedule(id, Instant.fromEpochMilliseconds(at)))

        // EXPECTED: durable record survives so re-granting notifications restores the alarm.
        // ACTUAL (bug): cancelInternal() at AndroidReminderScheduler.kt:72 deleted the record.
        assertTrue(
            "Durable reminder record lost after transient PermissionDenied (AndroidReminderScheduler.kt:72 cancelInternal)",
            prefs.contains(AndroidReminderScheduler.storageKey(id))
        )
        assertEquals(at, prefs.getLong(AndroidReminderScheduler.storageKey(id), -1))
    }
}
