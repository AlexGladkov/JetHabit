package feature.reminders.schedule

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
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
class NotificationSecurityAndroidTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val prefs = context.getSharedPreferences(AndroidReminderScheduler.PREFS, Context.MODE_PRIVATE)
    private val notifications = context.getSystemService(NotificationManager::class.java)

    @Test
    fun inProcessSecurityOracle() = runBlocking {
        clear()
        grantPermission()
        val pm = context.packageManager
        assertFalse(pm.getReceiverInfo(ComponentName(context, ReminderDeliveryReceiver::class.java), 0).exported)
        assertFalse(pm.getReceiverInfo(ComponentName(context, ReminderBootReceiver::class.java), 0).exported)

        val app = pm.getApplicationInfo(context.packageName, 0)
        assertTrue(app.flags and ApplicationInfo.FLAG_ALLOW_BACKUP != 0)
        assertNotEquals(0, context.resources.getIdentifier("backup_rules", "xml", context.packageName))
        if (Build.VERSION.SDK_INT >= 31) {
            assertNotEquals(0, context.resources.getIdentifier("data_extraction_rules", "xml", context.packageName))
        }

        val id = "acceptance.security.delivery"
        val sentinel = "acceptance.security.sentinel"
        val at = 4_102_444_800_000L
        AndroidReminderScheduler.putRecord(context, id, at)
        AndroidReminderScheduler.putRecord(context, sentinel, at + 1)
        val before = prefs.all.toMap()

        ReminderDeliveryCoordinator.deliver(context, Intent(context, ReminderDeliveryReceiver::class.java))
        ReminderDeliveryCoordinator.deliver(context, AndroidReminderScheduler.deliveryIntent(context, id, at + 1))
        ReminderDeliveryCoordinator.deliver(context, AndroidReminderScheduler.deliveryIntent(context, id, at).apply {
            data = Uri.parse("jethabit://reminder/wrong")
        })
        assertEquals(before, prefs.all)
        assertEquals(0, notifications.activeNotifications.count { it.id == AndroidReminderScheduler.digest(id).hashCode() })

        val exact = AndroidReminderScheduler.deliveryIntent(context, id, at)
        val pi = AndroidReminderScheduler.pendingIntent(context, exact)
        assertEquals(context.packageName, pi.creatorPackage)
        if (Build.VERSION.SDK_INT >= 31) assertTrue(pi.isImmutable)
        assertFalse(pi.isActivity)
        ReminderDeliveryCoordinator.deliver(context, exact)
        assertFalse(prefs.contains(AndroidReminderScheduler.idKey(id)))
        assertEquals(sentinel, prefs.getString(AndroidReminderScheduler.idKey(sentinel), null))
        val posted = notifications.activeNotifications.single { it.id == AndroidReminderScheduler.digest(id).hashCode() }
        assertEquals(AndroidReminderScheduler.CHANNEL_ID, posted.notification.channelId)
        assertEquals("Habit reminder", posted.notification.extras.getCharSequence("android.title").toString())
        assertFalse(posted.notification.extras.toString().contains(id))

        val count = notifications.activeNotifications.size
        ReminderDeliveryCoordinator.deliver(context, exact)
        assertEquals(count, notifications.activeNotifications.size)

        AndroidReminderScheduler.putRecord(context, "acceptance.security.denied", at)
        val deniedScheduler = AndroidReminderScheduler(
            context, AlarmInstaller { fail("permission denial must not arm") }, { false }, { 1_700_000_000_000L }
        )
        val denied = deniedScheduler.schedule("acceptance.security.denied", Instant.fromEpochMilliseconds(at))
        assertEquals(ReminderScheduleResult.PermissionDenied, denied)
        assertFalse(prefs.contains(AndroidReminderScheduler.idKey("acceptance.security.denied")))
        clear()
    }

    @Test
    fun seedExternalAttempt() = runBlocking {
        clear()
        grantPermission()
        val result = AndroidReminderScheduler(context).schedule(EXTERNAL_ID, Instant.fromEpochMilliseconds(EXTERNAL_AT))
        assertEquals(ReminderScheduleResult.Scheduled, result)
        context.getSharedPreferences(PROBE_PREFS, Context.MODE_PRIVATE).edit()
            .putInt("notificationCount", notifications.activeNotifications.size).commit()
        assertEquals(EXTERNAL_ID, prefs.getString(AndroidReminderScheduler.idKey(EXTERNAL_ID), null))
    }

    @Test
    fun verifyExternalAttempt() {
        assertEquals(EXTERNAL_ID, prefs.getString(AndroidReminderScheduler.idKey(EXTERNAL_ID), null))
        assertEquals(EXTERNAL_AT, prefs.getLong(AndroidReminderScheduler.storageKey(EXTERNAL_ID), -1))
        val expectedCount = context.getSharedPreferences(PROBE_PREFS, Context.MODE_PRIVATE)
            .getInt("notificationCount", -1)
        assertEquals(expectedCount, notifications.activeNotifications.size)
        clear()
    }

    private fun grantPermission() {
        if (Build.VERSION.SDK_INT >= 33) shell("pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
    }

    private fun shell(command: String) {
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        java.io.FileInputStream(descriptor.fileDescriptor).use { it.readBytes() }
        descriptor.close()
    }

    private fun clear() {
        notifications.cancelAll()
        prefs.edit().clear().commit()
        context.getSharedPreferences(PROBE_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
    }

    private companion object {
        const val EXTERNAL_ID = "acceptance.security.external"
        const val EXTERNAL_AT = 4_102_444_800_000L
        const val PROBE_PREFS = "acceptance_security_probe"
    }
}
