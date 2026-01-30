package di

import core.platform.ImagePicker
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun Module.provideImagePicker() {
    single<ImagePicker> {
        get<PlatformConfiguration>().imagePicker
    }
} 