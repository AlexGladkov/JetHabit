package di

import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.instance
import org.kodein.di.singleton

object PlatformSDK {
    private var _di: DirectDI? = null
    val di: DirectDI
        get() = requireNotNull(_di)

    fun init(
        configuration: PlatformConfiguration,
        appDatabase: Any? = null
    ) {
        val configModule = DI.Module("config") {
            bind<PlatformConfiguration>() with singleton { configuration }
            if (appDatabase != null) {
                bind<Any>("appDatabase") with singleton { appDatabase }
            }
        }

        val platformModule = DI.Module("platform") {
            provideImagePicker()
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