package feature.settings.di

import feature.settings.domain.ClearAllHabitsUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val settingsModule = DI.Module("SettingsModule") {
    bind<ClearAllHabitsUseCase>() with provider {
        ClearAllHabitsUseCase(instance(), instance())
    }
}