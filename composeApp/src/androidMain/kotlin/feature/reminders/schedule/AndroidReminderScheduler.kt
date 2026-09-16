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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import java.security.MessageDigest

internal data class ReminderAlarm(val reminderId: String, val triggerAt: Long, val intent: Intent)

internal fun interface AlarmInstaller {
    fun install(alarm: ReminderAlarm)
}

internal class AndroidAlarmInstaller(private val context: Context) : AlarmInstaller {
    override fun install(alarm: ReminderAlarm) {
        context.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.triggerAt,
            AndroidReminderScheduler.pendingIntent(context, alarm.intent)
        )
    }

    fun cancel(intent: Intent) {
        context.getSystemService(AlarmManager::class.java)
            .cancel(AndroidReminderScheduler.pendingIntent(context, intent))
    }
}

class AndroidReminderScheduler internal constructor(
    context: Context,
    private val alarmInstaller: AlarmInstaller,
    private val permissionCheck: (Context) -> Boolean,
    private val now: () -> Long
) : ReminderScheduler {
    constructor(context: Context) : this(
        context.applicationContext,
        AndroidAlarmInstaller(context.applicationContext),
        AndroidReminderScheduler::notificationsAllowed,
        System::currentTimeMillis
    )

    private val appContext = context.applicationContext

    override suspend fun schedule(reminderId: String, triggerAt: Instant): ReminderScheduleResult = withContext(Dispatchers.IO) {
        if (reminderId.isBlank()) return@withContext ReminderScheduleResult.Invalid("reminderId must not be blank")
        val triggerMillis = triggerAt.toEpochMilliseconds()
        if (triggerMillis <= now()) {
            cancelInternal(reminderId)
            return@withContext ReminderScheduleResult.Invalid("triggerAt must be in the future")
        }
        if (!permissionCheck(appContext)) {
            // Transient denial: retain the durable record; reconcile() reinstalls the alarm later.
            return@withContext ReminderScheduleResult.PermissionDenied
        }

        try {
            createChannel(appContext)
            if (!putRecord(appContext, reminderId, triggerMillis)) {
                return@withContext ReminderScheduleResult.Failed("Unable to persist reminder")
            }
            try {
                alarmInstaller.install(alarm(appContext, reminderId, triggerMillis))
            } catch (error: Exception) {
                removeRecord(appContext, reminderId)
                return@withContext ReminderScheduleResult.Failed(error.message ?: error::class.simpleName.orEmpty())
            }
            ReminderScheduleResult.Scheduled
        } catch (error: Exception) {
            removeRecord(appContext, reminderId)
            ReminderScheduleResult.Failed(error.message ?: error::class.simpleName.orEmpty())
        }
    }

    override suspend fun cancel(reminderId: String): ReminderScheduleResult = withContext(Dispatchers.IO) {
        if (reminderId.isBlank()) return@withContext ReminderScheduleResult.Invalid("reminderId must not be blank")
        cancelInternal(reminderId)
        ReminderScheduleResult.Cancelled
    }

    private fun cancelInternal(reminderId: String) {
        removeRecord(appContext, reminderId)
        runCatching {
            val intent = deliveryIntent(appContext, reminderId, null)
            (alarmInstaller as? AndroidAlarmInstaller)?.cancel(intent)
                ?: appContext.getSystemService(AlarmManager::class.java).cancel(pendingIntent(appContext, intent))
        }
    }

    companion object {
        internal const val PREFS = "reminder_scheduler"
        internal const val CHANNEL_ID = "habit_reminders"
        internal const val EXTRA_ID = "reminder_id"
        internal const val EXTRA_TRIGGER_AT = "trigger_at"
        internal const val URI_SCHEME = "jethabit"

        internal fun digest(id: String): String = MessageDigest.getInstance("SHA-256")
            .digest(id.encodeToByteArray()).joinToString("") { "%02x".format(it) }
        internal fun storageKey(id: String) = "at.${digest(id)}"
        internal fun idKey(id: String) = "id.${digest(id)}"

        internal fun deliveryIntent(context: Context, id: String, triggerAt: Long?): Intent =
            Intent(context, ReminderDeliveryReceiver::class.java).apply {
                data = Uri.parse("$URI_SCHEME://reminder/${digest(id)}")
                putExtra(EXTRA_ID, id)
                triggerAt?.let { putExtra(EXTRA_TRIGGER_AT, it) }
            }

        internal fun alarm(context: Context, id: String, triggerAt: Long) =
            ReminderAlarm(id, triggerAt, deliveryIntent(context, id, triggerAt))

        internal fun pendingIntent(context: Context, intent: Intent): PendingIntent = PendingIntent.getBroadcast(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        internal fun pendingIntent(context: Context, id: String, triggerAt: Long? = null): PendingIntent =
            pendingIntent(context, deliveryIntent(context, id, triggerAt))

        internal fun putRecord(context: Context, id: String, at: Long): Boolean =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .putLong(storageKey(id), at).putString(idKey(id), id).commit()

        internal fun removeRecord(context: Context, id: String): Boolean =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
                .remove(storageKey(id)).remove(idKey(id)).commit()

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

internal object ReminderDeliveryCoordinator {
    fun deliver(context: Context, intent: Intent) {
        val id = intent.getStringExtra(AndroidReminderScheduler.EXTRA_ID) ?: return
        val prefs = context.getSharedPreferences(AndroidReminderScheduler.PREFS, Context.MODE_PRIVATE)
        val expectedAt = prefs.getLong(AndroidReminderScheduler.storageKey(id), -1L)
        if (expectedAt < 0L ||
            prefs.getString(AndroidReminderScheduler.idKey(id), null) != id ||
            intent.getLongExtra(AndroidReminderScheduler.EXTRA_TRIGGER_AT, -1L) != expectedAt ||
            intent.data?.scheme != AndroidReminderScheduler.URI_SCHEME ||
            intent.data?.host != "reminder" ||
            intent.data?.lastPathSegment != AndroidReminderScheduler.digest(id)
        ) return

        if (!AndroidReminderScheduler.notificationsAllowed(context)) return
        AndroidReminderScheduler.createChannel(context)
        val notification = NotificationCompat.Builder(context, AndroidReminderScheduler.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Habit reminder")
            .setContentText("It is time for your habit")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(AndroidReminderScheduler.digest(id).hashCode(), notification)
        AndroidReminderScheduler.removeRecord(context, id)
    }
}

class ReminderDeliveryReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = ReminderDeliveryCoordinator.deliver(context, intent)
}

internal object ReminderRecoveryCoordinator {
    fun reconcile(context: Context, installer: AlarmInstaller, now: Long) {
        val prefs = context.getSharedPreferences(AndroidReminderScheduler.PREFS, Context.MODE_PRIVATE)
        val all = prefs.all
        val digests = all.keys.mapNotNull { key ->
            when {
                key.startsWith("id.") -> key.removePrefix("id.")
                key.startsWith("at.") -> key.removePrefix("at.")
                else -> null
            }
        }.toSet()

        for (digest in digests) {
            val idKey = "id.$digest"
            val atKey = "at.$digest"
            val id = all[idKey] as? String
            val at = all[atKey] as? Long
            if (id == null || at == null || AndroidReminderScheduler.digest(id) != digest || at <= now) {
                prefs.edit().remove(idKey).remove(atKey).commit()
                continue
            }
            runCatching { installer.install(AndroidReminderScheduler.alarm(context, id, at)) }
            // A valid record is retained when reinstallation fails so a later reconciliation can retry it.
        }
    }
}

class ReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ReminderRecoveryCoordinator.reconcile(
                    context.applicationContext,
                    AndroidAlarmInstaller(context.applicationContext),
                    System.currentTimeMillis()
                )
            } finally {
                pending.finish()
            }
        }
    }
}
