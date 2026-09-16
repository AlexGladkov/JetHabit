package di

import core.platform.ImagePicker
import feature.reminders.domain.schedule.ReminderScheduler
import feature.reminders.schedule.AndroidReminderScheduler
import feature.reminders.schedule.ReminderPermissionRequester
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

actual fun DI.Builder.provideImagePicker() {
    bind<ImagePicker>() with singleton {
        instance<PlatformConfiguration>().imagePicker
    }
}

actual fun DI.Builder.provideReminderScheduler() {
    bind<ReminderScheduler>() with singleton {
        val config = instance<PlatformConfiguration>()
        val requester = config.notificationPermissionRequester
        AndroidReminderScheduler(
            context = config.application,
            permissionRequest = { requester.request() }
        )
    }
}