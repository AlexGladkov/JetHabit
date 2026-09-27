package di

import feature.share.platform.ShareService
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

actual fun DI.Builder.provideShareService() {
    bind<ShareService>() with singleton {
        val configuration = instance<PlatformConfiguration>()
        ShareService(configuration.application)
    }
}
