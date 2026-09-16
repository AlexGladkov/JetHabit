package di

import android.app.Application
import androidx.activity.ComponentActivity
import core.platform.ImagePicker
import feature.reminders.schedule.ReminderPermissionRequester

actual class PlatformConfiguration(
    val application: Application,
    val activity: ComponentActivity,
    val imagePicker: ImagePicker,
    val notificationPermissionRequester: ReminderPermissionRequester
) {
    actual val platform: Platform = Platform.Android
    actual val appName: String = "JetHabit"
}