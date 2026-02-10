package di

import core.auth.AuthRepository
import core.auth.VkAuthProvider
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

expect fun DI.Builder.provideVkAuthProvider()

val authModule = DI.Module("authModule") {
    provideVkAuthProvider()
    bind<AuthRepository>() with singleton {
        AuthRepository(
            authProvider = instance(),
            userProfileDao = instance()
        )
    }
}
