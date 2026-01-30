package di

import org.koin.dsl.module

val coreModule = module {
    includes(
        serializationModule
    )
}