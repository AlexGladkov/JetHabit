package di

import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

object PlatformSDK {
    private var _koin: Koin? = null
    val koin: Koin
        get() = requireNotNull(_koin)

    fun init(
        configuration: PlatformConfiguration,
        appDatabase: Any? = null
    ) {
        // Stop any existing Koin instance to allow reinitialization
        if (GlobalContext.getOrNull() != null) {
            stopKoin()
        }

        val configModule = module {
            single<PlatformConfiguration> { configuration }
            if (appDatabase != null) {
                single(qualifier = named("appDatabase")) { appDatabase }
            }
        }

        val platformModule = module {
            provideImagePicker()
        }

        _koin = startKoin {
            modules(
                configModule,
                platformModule,
                databaseModule,
                featureModule
            )
        }.koin
    }

    inline fun <reified T> instance(): T {
        return koin.get()
    }
}