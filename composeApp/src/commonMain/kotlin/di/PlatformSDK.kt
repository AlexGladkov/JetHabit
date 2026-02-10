package di

import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.instance
import org.kodein.di.singleton

object PlatformSDK {
    private var _di: DI? = null
    private var _directDI: DirectDI? = null

    val di: DI
        get() = requireNotNull(_di)

    val directDI: DirectDI
        get() = requireNotNull(_directDI)

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

        val kodeinDI = DI {
            importAll(
                configModule,
                platformModule,
                databaseModule(),
                featureModule()
            )
        }

        _di = kodeinDI
        _directDI = kodeinDI.direct
    }

    inline fun <reified T> instance(): T {
        return directDI.instance()
    }
}