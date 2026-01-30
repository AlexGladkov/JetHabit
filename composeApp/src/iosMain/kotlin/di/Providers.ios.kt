package di

import core.auth.VkAuthProvider
import core.platform.IOSImagePicker
import core.platform.ImagePicker
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

actual fun DI.Builder.provideImagePicker(platform: Platform) {
    bind<ImagePicker>() with singleton { IOSImagePicker() }
}

actual fun DI.Builder.provideVkAuthProvider() {
    bind<VkAuthProvider>() with singleton { VkAuthProvider() }
} 