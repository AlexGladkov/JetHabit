package feature.reminders.schedule

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReminderDeliveryDurabilityRegressionAndroidTest {
    private val appContext = ApplicationProvider.getApplicationContext<Context>()
    private val prefs = appContext.getSharedPreferences(AndroidReminderScheduler.PREFS, Context.MODE_PRIVATE)

    @After
    fun clearRecords() {
        prefs.edit().clear().commit()
    }

    @Test
    fun deniedNotificationPermissionKeepsReminderRecordForRetry() {
        assumeTrue("POST_NOTIFICATIONS is runtime checked on API 33+", Build.VERSION.SDK_INT >= 33)
        val deniedContext = object : ContextWrapper(appContext) {
            override fun checkPermission(permission: String, pid: Int, uid: Int): Int =
                if (permission == Manifest.permission.POST_NOTIFICATIONS) PackageManager.PERMISSION_DENIED
                else super.checkPermission(permission, pid, uid)
        }
        val id = "regression.delivery.permission-denied"
        val at = 4_102_444_800_000L
        assertTrue(AndroidReminderScheduler.putRecord(deniedContext, id, at))

        ReminderDeliveryCoordinator.deliver(
            deniedContext,
            AndroidReminderScheduler.deliveryIntent(deniedContext, id, at)
        )

        assertEquals(id, prefs.getString(AndroidReminderScheduler.idKey(id), null))
        assertEquals(at, prefs.getLong(AndroidReminderScheduler.storageKey(id), -1L))
    }

    @Test
    fun notificationPostingExceptionKeepsReminderRecordForRetry() {
        grantNotificationPermission()
        assumeTrue("notifications must be enabled to reach the posting path", NotificationManagerCompat.from(appContext).areNotificationsEnabled())
        val throwingContext = object : ContextWrapper(appContext) {
            var notificationServiceLookups = 0
            var injected = false

            override fun checkPermission(permission: String, pid: Int, uid: Int): Int =
                if (permission == Manifest.permission.POST_NOTIFICATIONS) PackageManager.PERMISSION_GRANTED
                else super.checkPermission(permission, pid, uid)

            override fun getSystemService(name: String): Any? {
                if (name == Context.NOTIFICATION_SERVICE && ++notificationServiceLookups == 3) {
                    injected = true
                    throw SecurityException("deterministic notify failure")
                }
                return super.getSystemService(name)
            }
        }
        val id = "regression.delivery.notify-exception"
        val at = 4_102_444_800_000L
        assertTrue(AndroidReminderScheduler.putRecord(throwingContext, id, at))

        runCatching {
            ReminderDeliveryCoordinator.deliver(
                throwingContext,
                AndroidReminderScheduler.deliveryIntent(throwingContext, id, at)
            )
        }

        assertTrue("the notification posting path must throw", throwingContext.injected)
        assertEquals(id, prefs.getString(AndroidReminderScheduler.idKey(id), null))
        assertEquals(at, prefs.getLong(AndroidReminderScheduler.storageKey(id), -1L))
    }

    private fun grantNotificationPermission() {
        if (Build.VERSION.SDK_INT < 33) return
        val command = "pm grant ${appContext.packageName} ${Manifest.permission.POST_NOTIFICATIONS}"
        val descriptor = InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(command)
        java.io.FileInputStream(descriptor.fileDescriptor).use { it.readBytes() }
        descriptor.close()
    }
}
