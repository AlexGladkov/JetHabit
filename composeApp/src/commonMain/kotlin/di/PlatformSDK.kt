package di

import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.instance
import org.kodein.di.singleton
import org.kodein.di.provider

object PlatformSDK {
    private var _di: DirectDI? = null
    private var configuration: PlatformConfiguration? = null
    var initializationCount: Int = 0
        private set
    var initializedDatabase: Any? = null
        private set
    val di: DirectDI
        get() = requireNotNull(_di)

    fun init(
        configuration: PlatformConfiguration,
        appDatabase: Any? = null
    ) {
        if (_di != null) {
            this.configuration = configuration
            return
        }
        initializationCount++
        initializedDatabase = appDatabase
        this.configuration = configuration
        val configModule = DI.Module("config") {
            bind<PlatformConfiguration>() with provider {
                requireNotNull(PlatformSDK.configuration)
            }
            if (appDatabase != null) {
                bind<Any>("appDatabase") with singleton { appDatabase }
            }
        }

        val platformModule = DI.Module("platform") {
            provideImagePicker()
            provideShareService()
        }

        _di = DI {
            importAll(
                configModule,
                platformModule,
                databaseModule(),
                featureModule()
            )
        }.direct
    }

    inline fun <reified T> instance(): T {
        return di.instance()
    }
}