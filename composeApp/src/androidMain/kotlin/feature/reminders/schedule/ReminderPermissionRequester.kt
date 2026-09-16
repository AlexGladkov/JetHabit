package feature.reminders.schedule

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Runtime POST_NOTIFICATIONS request entry point for the reminder enable flow (API 33+).
 * Resumes with the user's grant result; on API < 33 or when already granted it completes
 * immediately with `true` without showing any dialog. Denial is non-fatal: callers fall
 * back to the existing ReminderScheduleResult.PermissionDenied path.
 */
class ReminderPermissionRequester(private val activity: ComponentActivity) {

    private var callback: ((Boolean) -> Unit)? = null

    private val launcher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        callback?.invoke(granted)
        callback = null
    }

    suspend fun request(): Boolean = suspendCancellableCoroutine { continuation ->
        if (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
                activity, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }
        callback = { granted ->
            if (continuation.isActive) continuation.resume(granted)
        }
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
