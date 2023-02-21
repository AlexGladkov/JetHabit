package di

import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

val serializationModule = DI.Module("serializationModule") {
    bind<Json>() with singleton {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
}