package di

import core.platform.ImagePicker
import core.platform.ShareService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

actual fun DI.Builder.provideImagePicker() {
    bind<ImagePicker>() with singleton {
        object : ImagePicker {
            override suspend fun pickImage(): String? = null
            override suspend fun takePhoto(): String? = null
        }
    }
}

actual fun DI.Builder.provideShareService() {
    bind<ShareService>() with singleton { ShareService() }
}
