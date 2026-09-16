package feature.reminders.schedule

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import feature.reminders.domain.schedule.ReminderScheduleResult
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReminderSchedulerAndroidTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefs = context.getSharedPreferences(AndroidReminderScheduler.PREFS, Context.MODE_PRIVATE)
    private val now = 1_700_000_000_000L

    @After
    fun clean() {
        prefs.edit().clear().commit()
    }

    @Test
    fun schedulerContractOracle() = runBlocking {
        val installed = mutableListOf<ReminderAlarm>()
        val scheduler = AndroidReminderScheduler(context, AlarmInstaller { installed += it }, { true }, { now })
        val aAt = now + 60_000
        val bAt = now + 120_000

        assertEquals(ReminderScheduleResult.Scheduled, scheduler.schedule("acceptance.scheduler.a", Instant.fromEpochMilliseconds(aAt)))
        assertEquals(ReminderScheduleResult.Scheduled, scheduler.schedule("acceptance.scheduler.b", Instant.fromEpochMilliseconds(bAt)))
        assertEquals(aAt, prefs.getLong(AndroidReminderScheduler.storageKey("acceptance.scheduler.a"), -1))
        assertEquals("acceptance.scheduler.a", prefs.getString(AndroidReminderScheduler.idKey("acceptance.scheduler.a"), null))
        assertEquals(2, installed.size)
        assertEquals(AlarmManager.RTC_WAKEUP, AlarmManager.RTC_WAKEUP)
        assertEquals(aAt, installed[0].triggerAt)
        assertEquals(ReminderDeliveryReceiver::class.java.name, installed[0].intent.component?.className)
        assertEquals(context.packageName, installed[0].intent.component?.packageName)
        assertEquals("jethabit://reminder/${AndroidReminderScheduler.digest("acceptance.scheduler.a")}", installed[0].intent.data.toString())

        val aPi = AndroidReminderScheduler.pendingIntent(context, installed[0].intent)
        val bPi = AndroidReminderScheduler.pendingIntent(context, installed[1].intent)
        assertNotEquals(aPi, bPi)
        if (android.os.Build.VERSION.SDK_INT >= 31) assertTrue(aPi.isImmutable)
        assertFalse(aPi.isActivity)

        // A new adapter observes the same process-independent SharedPreferences record.
        AndroidReminderScheduler(context, AlarmInstaller { }, { true }, { now })
        assertTrue(prefs.contains(AndroidReminderScheduler.storageKey("acceptance.scheduler.b")))

        assertEquals(ReminderScheduleResult.Cancelled, scheduler.cancel("acceptance.scheduler.a"))
        assertEquals(ReminderScheduleResult.Cancelled, scheduler.cancel("acceptance.scheduler.a"))
        assertFalse(prefs.contains(AndroidReminderScheduler.storageKey("acceptance.scheduler.a")))
        assertEquals(bAt, prefs.getLong(AndroidReminderScheduler.storageKey("acceptance.scheduler.b"), -1))

        assertTrue(scheduler.schedule("", Instant.fromEpochMilliseconds(aAt)) is ReminderScheduleResult.Invalid)
        assertTrue(scheduler.schedule("past", Instant.fromEpochMilliseconds(now)) is ReminderScheduleResult.Invalid)
        assertFalse(prefs.contains(AndroidReminderScheduler.storageKey("past")))

        val denied = AndroidReminderScheduler(context, AlarmInstaller { fail("must not arm") }, { false }, { now })
        assertEquals(ReminderScheduleResult.PermissionDenied, denied.schedule("denied", Instant.fromEpochMilliseconds(aAt)))
        // Transient denial retains the durable record (delivery-path symmetry; reconcile() reinstalls later).
        assertEquals(aAt, prefs.getLong(AndroidReminderScheduler.storageKey("denied"), -1))
        assertEquals("denied", prefs.getString(AndroidReminderScheduler.idKey("denied"), null))

        val failing = AndroidReminderScheduler(context, AlarmInstaller { throw SecurityException("synthetic") }, { true }, { now })
        assertTrue(failing.schedule("acceptance.scheduler.failure", Instant.fromEpochMilliseconds(aAt)) is ReminderScheduleResult.Failed)
        assertFalse(prefs.contains(AndroidReminderScheduler.storageKey("acceptance.scheduler.failure")))
        assertFalse(prefs.contains(AndroidReminderScheduler.idKey("acceptance.scheduler.failure")))
        assertEquals(bAt, prefs.getLong(AndroidReminderScheduler.storageKey("acceptance.scheduler.b"), -1))
    }
}
