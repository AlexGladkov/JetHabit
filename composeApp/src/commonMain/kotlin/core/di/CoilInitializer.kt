package core.di

import coil3.ImageLoader
import coil3.PlatformContext

fun initializeCoil(context: PlatformContext) {
    ImageLoader.Builder(context)
        .build()
} 