package di

import feature.daily.di.dailyModule
import feature.detail.di.detailModule
import feature.habits.di.habitModule
import feature.settings.di.settingsModule
import org.kodein.di.DI

val featureModule = DI.Module("FeatureModule") {
    importAll(
        dailyModule,
        habitModule,
        settingsModule,
        detailModule
    )
}