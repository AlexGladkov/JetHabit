package feature.reminders.schedule

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import feature.reminders.domain.schedule.ReminderScheduleResult
import feature.reminders.domain.schedule.ReminderScheduler
import kotlinx.datetime.Instant
import java.security.MessageDigest

class AndroidReminderScheduler(context: Context) : ReminderScheduler {
    private val appContext = context.applicationContext
    private val alarms = appContext.getSystemService(AlarmManager::class.java)

    override suspend fun schedule(reminderId: String, triggerAt: Instant): ReminderScheduleResult {
        if (reminderId.isBlank()) return ReminderScheduleResult.Invalid("reminderId must not be blank")
        val triggerMillis = triggerAt.toEpochMilliseconds()
        if (triggerMillis <= System.currentTimeMillis()) {
            cancel(reminderId)
            return ReminderScheduleResult.Invalid("triggerAt must be in the future")
        }
        if (!notificationsAllowed(appContext)) {
            cancel(reminderId)
            return ReminderScheduleResult.PermissionDenied
        }
        return runCatching {
            createChannel(appContext)
            appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putLong(storageKey(reminderId), triggerMillis)
                .putString(idKey(reminderId), reminderId)
                .apply()
            alarms.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent(appContext, reminderId, triggerMillis)
            )
            ReminderScheduleResult.Scheduled
        }.getOrElse { ReminderScheduleResult.Failed(it.message ?: it::class.simpleName.orEmpty()) }
    }

    override suspend fun cancel(reminderId: String): ReminderScheduleResult {
        if (reminderId.isBlank()) return ReminderScheduleResult.Invalid("reminderId must not be blank")
        alarms.cancel(pendingIntent(appContext, reminderId))
        appContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .remove(storageKey(reminderId)).remove(idKey(reminderId)).apply()
        return ReminderScheduleResult.Cancelled
    }

    companion object {
        internal const val PREFS = "reminder_scheduler"
        internal const val CHANNEL_ID = "habit_reminders"
        internal const val EXTRA_ID = "reminder_id"
        internal const val EXTRA_TRIGGER_AT = "trigger_at"

        internal fun digest(id: String): String = MessageDigest.getInstance("SHA-256")
            .digest(id.encodeToByteArray()).joinToString("") { "%02x".format(it) }
        internal fun storageKey(id: String) = "at.${digest(id)}"
        internal fun idKey(id: String) = "id.${digest(id)}"
        internal fun pendingIntent(context: Context, id: String, triggerAt: Long? = null): PendingIntent {
            val intent = Intent(context, ReminderDeliveryReceiver::class.java).apply {
                data = Uri.parse("jethabit://reminder/${digest(id)}")
                putExtra(EXTRA_ID, id)
                triggerAt?.let { putExtra(EXTRA_TRIGGER_AT, it) }
            }
            return PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        internal fun notificationsAllowed(context: Context): Boolean =
            (Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) && NotificationManagerCompat.from(context).areNotificationsEnabled()

        internal fun createChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= 26) {
                context.getSystemService(NotificationManager::class.java).createNotificationChannel(
                    NotificationChannel(CHANNEL_ID, "Habit reminders", NotificationManager.IMPORTANCE_DEFAULT)
                )
            }
        }
    }
}

class ReminderDeliveryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra(AndroidReminderScheduler.EXTRA_ID) ?: return
        val prefs = context.getSharedPreferences(AndroidReminderScheduler.PREFS, Context.MODE_PRIVATE)
        val key = AndroidReminderScheduler.storageKey(id)
        val expectedAt = prefs.getLong(key, -1L)
        if (expectedAt < 0L ||
            intent.getLongExtra(AndroidReminderScheduler.EXTRA_TRIGGER_AT, -1L) != expectedAt ||
            intent.data?.lastPathSegment != AndroidReminderScheduler.digest(id)
        ) return
        prefs.edit().remove(key).remove(AndroidReminderScheduler.idKey(id)).apply()
        if (!AndroidReminderScheduler.notificationsAllowed(context)) return
        AndroidReminderScheduler.createChannel(context)
        val notification = NotificationCompat.Builder(context, AndroidReminderScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Habit reminder")
            .setContentText("It is time for your habit")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(AndroidReminderScheduler.digest(id).hashCode(), notification)
    }
}

class ReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        val pending = goAsync()
        try {
            val prefs = context.getSharedPreferences(AndroidReminderScheduler.PREFS, Context.MODE_PRIVATE)
            val now = System.currentTimeMillis()
            prefs.all.filterKeys { it.startsWith("id.") }.values.filterIsInstance<String>().forEach { id ->
                val at = prefs.getLong(AndroidReminderScheduler.storageKey(id), -1L)
                if (at > now) {
                    context.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, at, AndroidReminderScheduler.pendingIntent(context, id, at)
                    )
                } else {
                    prefs.edit().remove(AndroidReminderScheduler.storageKey(id)).remove(AndroidReminderScheduler.idKey(id)).apply()
                }
            }
        } finally {
            pending.finish()
        }
    }
}
