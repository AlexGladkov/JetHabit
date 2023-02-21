package di

import org.kodein.di.DI

val coreModule = DI.Module("coreModule") {
    importAll(
        serializationModule,
        databaseModule
    )
}