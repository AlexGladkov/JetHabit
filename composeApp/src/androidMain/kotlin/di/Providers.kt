package di

import core.auth.VkAuthProvider
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

actual fun DI.Builder.provideVkAuthProvider() {
    bind<VkAuthProvider>() with singleton {
        VkAuthProvider(instance<PlatformConfiguration>().activity)
    }
} 