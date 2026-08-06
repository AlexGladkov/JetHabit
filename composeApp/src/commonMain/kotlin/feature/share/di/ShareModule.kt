package feature.share.di

import feature.share.domain.CalculateStreaksUseCase
import feature.share.domain.GetTopHabitsForSharingUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

/**
 * Kodein DI module for the share feature.
 * Provides all dependencies for sharing habit achievements.
 */
val shareModule = DI.Module("ShareModule") {
    // Domain layer - Use Cases
    bind<CalculateStreaksUseCase>() with provider {
        CalculateStreaksUseCase(
            habitDao = instance(),
            dailyDao = instance()
        )
    }

    bind<GetTopHabitsForSharingUseCase>() with provider {
        GetTopHabitsForSharingUseCase(
            habitDao = instance(),
            dailyDao = instance(),
            calculateStreaksUseCase = instance()
        )
    }

    // Platform layer - ShareService is provided via platform-specific modules
    // See: provideShareService() in Providers.kt (expect/actual)
}
