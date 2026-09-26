package di

import core.platform.ImagePicker
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.kodein.di.provider

actual fun DI.Builder.provideImagePicker() {
    // ActivityResult launchers belong to the current Activity; resolve on each injection
    // so recreation cannot retain a destroyed Activity's picker.
    bind<ImagePicker>() with provider {
        instance<PlatformConfiguration>().imagePicker
    }
} 