package di

import core.platform.ImagePicker
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

actual fun DI.Builder.provideImagePicker() {
    bind<ImagePicker>() with singleton { 
        instance<PlatformConfiguration>().imagePicker
    }
} 