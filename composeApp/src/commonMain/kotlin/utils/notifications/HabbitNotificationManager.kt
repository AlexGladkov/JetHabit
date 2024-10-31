package utils.notifications

expect class HabbitNotificationManager {
    fun sendNotification(title: String, content: String)

    companion object {
        fun create(): HabbitNotificationManager
    }
}