package di

import core.platform.DesktopImagePicker
import core.platform.ImagePicker
import feature.reminders.domain.schedule.ReminderScheduler
import feature.reminders.domain.schedule.UnsupportedReminderScheduler
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

actual fun DI.Builder.provideImagePicker() {
    bind<ImagePicker>() with singleton { DesktopImagePicker() }
}

actual fun DI.Builder.provideReminderScheduler() {
    bind<ReminderScheduler>() with singleton { UnsupportedReminderScheduler() }
}