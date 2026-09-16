package feature.reminders.schedule

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import feature.reminders.domain.schedule.ReminderScheduleResult
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * REPRODUCTION of bug: on API 33+ POST_NOTIFICATIONS is a runtime permission, but the app NEVER
 * requests it (no requestPermissions / rememberLauncherForActivityResult / RequestPermission
 * contract anywhere in production code). Fresh install => notificationsAllowed() == false =>
 * AndroidReminderScheduler.schedule() ALWAYS returns PermissionDenied (AndroidReminderScheduler.kt:71)
 * and the reminder feature is undeliverable.
 *
 * DESIRED behavior (regression gate): a runtime POST_NOTIFICATIONS request entry point must exist
 * in the enable/schedule flow, so a user can grant and reach ReminderScheduleResult.Scheduled.
 */
@RunWith(AndroidJUnit4::class)
class PostNotificationsRuntimeRequestReproTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()

    private fun shell(cmd: String) =
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(cmd).close()

    @Test
    fun scheduleOnApi33PlusIsBlockedForeverBecauseNoRuntimeRequestExists() = runBlocking {
        assumeTrue("POST_NOTIFICATIONS is runtime-gated only on API 33+", Build.VERSION.SDK_INT >= 33)

        // 1. Symptom (deterministic): with the permission revoked, the real default scheduler
        //    (real permissionCheck = notificationsAllowed) always returns PermissionDenied.
        shell("pm revoke ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}")
        assertEquals(
            PackageManager.PERMISSION_DENIED,
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
        )
        val scheduler = AndroidReminderScheduler(context)
        val at = System.currentTimeMillis() + 60_000
        val denied = scheduler.schedule("repro.postnotifications", Instant.fromEpochMilliseconds(at))
        assertEquals(
            "BUG SYMPTOM: schedule() returns PermissionDenied because no runtime request ever occurs",
            ReminderScheduleResult.PermissionDenied,
            denied
        )

        // 2. ROOT CAUSE (this is the assertion that FAILS now): the app must expose a runtime
        //    POST_NOTIFICATIONS request entry point in the reminder enable flow. Today no such
        //    code exists anywhere in the production sources (grep of requestPermissions /
        //    rememberLauncher / RequestPermission over src/ yields no production hits), so the
        //    user has no in-app path to grant and the denied state above is permanent.
        val fixPresent = try {
            Class.forName("feature.reminders.schedule.ReminderPermissionRequester")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
        assertTrue(
            "BUG ROOT CAUSE: no runtime POST_NOTIFICATIONS request entry point exists; " +
                "the fix must add one (e.g. ReminderPermissionRequester) wired into the enable flow",
            fixPresent
        )
    }
}
