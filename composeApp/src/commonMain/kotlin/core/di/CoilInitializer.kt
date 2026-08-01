package core.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader

fun initializeCoil(context: PlatformContext) {
    SingletonImageLoader.setSafe {
        ImageLoader.Builder(context)
            .build()
    }
}
