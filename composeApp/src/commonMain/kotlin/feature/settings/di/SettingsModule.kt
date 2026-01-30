package feature.settings.di

import feature.settings.domain.ClearAllHabitsUseCase
import org.koin.dsl.module

val settingsModule = module {
    factory<ClearAllHabitsUseCase> {
        ClearAllHabitsUseCase(get(), get())
    }
}