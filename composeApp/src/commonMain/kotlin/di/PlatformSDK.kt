package di

import core.database.AppDatabase
import data.features.daily.dailyModule
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.singleton

object PlatformSDK {

    fun init(
        configuration: PlatformConfiguration,
        appDatabase: AppDatabase
    ) {
        val umbrellaModule = DI.Module("umbrella") {
            bind<PlatformConfiguration>() with singleton { configuration }
            bind<AppDatabase>() with singleton { appDatabase }
        }

        Inject.createDependencies(
            DI {
                importAll(
                    umbrellaModule,
                    coreModule,
                    dailyModule,
                    featureModule
                )
            }.direct
        )
    }
}