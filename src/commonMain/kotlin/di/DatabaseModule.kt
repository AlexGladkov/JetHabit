package di

import data.DriverFactory
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

val databaseModule = DI.Module("databaseModule") {
    bind<DriverFactory>() with singleton {
        DriverFactory(
    }
}