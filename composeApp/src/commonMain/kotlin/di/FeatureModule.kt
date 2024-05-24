package di

import feature.daily.di.dailyModule
import feature.habits.di.habitModule
import org.kodein.di.DI

val featureModule = DI.Module("FeatureModule") {
    importAll(
        dailyModule,
        habitModule
    )
}