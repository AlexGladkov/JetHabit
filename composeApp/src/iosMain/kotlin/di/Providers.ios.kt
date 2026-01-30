package di

import core.platform.IOSImagePicker
import core.platform.ImagePicker
import org.koin.core.module.Module

actual fun Module.provideImagePicker() {
    single<ImagePicker> { IOSImagePicker() }
} 