package data.features.habit

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val habitModule = DI.Module("habitModule") {
    bind<HabitRepository>() with provider {
        HabitRepository(instance())
    }
}