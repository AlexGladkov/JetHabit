package di

import core.platform.DesktopImagePicker
import core.platform.ImagePicker
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

actual fun DI.Builder.provideImagePicker(platform: Platform) {
    bind<ImagePicker>() with singleton { DesktopImagePicker() }
} 