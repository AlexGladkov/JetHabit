package utils.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

actual class HabbitNotificationManager private constructor(private val context: Context) {
    actual fun sendNotification(title: String, content: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
            Notification.Builder(context, CHANNEL_ID)
        } else {
            Notification.Builder(context)
        }

        builder.setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }

    actual companion object {
        private lateinit var appContext: Context
        private const val CHANNEL_ID = "Habit_tracker_notifications"
        private const val CHANNEL_NAME = "Habit notifications"

        actual fun create(): HabbitNotificationManager {
            return HabbitNotificationManager(appContext)
        }

        fun initialize(context: Context) {
            appContext = context
        }
    }
}