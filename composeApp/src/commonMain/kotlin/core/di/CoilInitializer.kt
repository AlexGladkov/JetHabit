package core.di

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi

fun initializeCoil(context: PlatformContext) {
    ImageLoader.Builder(context)
        .build()
} 