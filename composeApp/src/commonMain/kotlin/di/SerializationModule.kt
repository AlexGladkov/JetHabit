package di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

val serializationModule = module {
    single<Json> {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
}