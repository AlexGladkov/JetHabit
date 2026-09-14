package feature.reminders.schedule

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import feature.reminders.domain.schedule.ReminderScheduleResult
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.Assert.*
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class BootResyncAndroidTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefs = context.getSharedPreferences(AndroidReminderScheduler.PREFS, Context.MODE_PRIVATE)

    @Test
    fun logicOracle() {
        prefs.edit().clear().commit()
        val now = 1_700_000_000_000L
        fun seed(id: String, at: Long) {
            prefs.edit().putString(AndroidReminderScheduler.idKey(id), id)
                .putLong(AndroidReminderScheduler.storageKey(id), at).commit()
        }
        seed("acceptance.boot.fail", now + 10_000)
        seed("acceptance.boot.good", now + 20_000)
        seed("acceptance.boot.expired", now - 1)
        prefs.edit().putString("id.malformed", "wrong-digest")
            .putString("at.malformed", "not-a-long")
            .putLong("at.orphan", now + 30_000).commit()

        val installed = mutableListOf<ReminderAlarm>()
        ReminderRecoveryCoordinator.reconcile(context, AlarmInstaller {
            if (it.reminderId == "acceptance.boot.fail") throw IllegalStateException("synthetic")
            installed += it
        }, now)

        assertEquals(listOf("acceptance.boot.good"), installed.map { it.reminderId })
        assertTrue(prefs.contains(AndroidReminderScheduler.storageKey("acceptance.boot.fail")))
        assertTrue(prefs.contains(AndroidReminderScheduler.storageKey("acceptance.boot.good")))
        assertFalse(prefs.contains(AndroidReminderScheduler.storageKey("acceptance.boot.expired")))
        assertFalse(prefs.contains("id.malformed"))
        assertFalse(prefs.contains("at.malformed"))
        assertFalse(prefs.contains("at.orphan"))
        prefs.edit().clear().commit()
    }

    @Test
    fun seedFutureProductionFixtureForHostReboot() = runBlocking {
        val args = InstrumentationRegistry.getArguments()
        val id = args.getString("reminderId") ?: DEFAULT_ID
        val at = args.getString("triggerAt")?.toLongOrNull() ?: DEFAULT_AT
        grantNotificationPermission()
        val result = AndroidReminderScheduler(context).schedule(id, Instant.fromEpochMilliseconds(at))
        assertEquals(ReminderScheduleResult.Scheduled, result)
        assertEquals(id, prefs.getString(AndroidReminderScheduler.idKey(id), null))
        assertEquals(at, prefs.getLong(AndroidReminderScheduler.storageKey(id), -1))
    }

    @Test
    fun verifyFutureFixtureAfterRealBootReadOnly() {
        val args = InstrumentationRegistry.getArguments()
        val id = args.getString("reminderId") ?: DEFAULT_ID
        val at = args.getString("triggerAt")?.toLongOrNull() ?: DEFAULT_AT
        // Intentionally read-only: the host independently proves the derived AlarmManager projection.
        val snapshot = prefs.all
        assertEquals(id, snapshot[AndroidReminderScheduler.idKey(id)])
        assertEquals(at, snapshot[AndroidReminderScheduler.storageKey(id)])
    }

    private fun grantNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation
                .executeShellCommand("pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
            java.io.FileInputStream(descriptor.fileDescriptor).use { it.readBytes() }
            descriptor.close()
        }
    }

    private companion object {
        const val DEFAULT_ID = "acceptance.boot.synthetic"
        const val DEFAULT_AT = 4_102_444_800_000L
    }
}
