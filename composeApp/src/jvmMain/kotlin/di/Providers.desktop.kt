package di

import core.auth.VkAuthProvider
import core.platform.DesktopImagePicker
import core.platform.ImagePicker
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

actual fun DI.Builder.provideImagePicker() {
    bind<ImagePicker>() with singleton { DesktopImagePicker() }
}

actual fun DI.Builder.provideVkAuthProvider() {
    bind<VkAuthProvider>() with singleton { VkAuthProvider() }
} 