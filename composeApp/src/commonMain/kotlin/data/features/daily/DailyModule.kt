package data.features.daily

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val dailyModule = DI.Module("daily") {
    bind<DailyRepository>() with provider {
        DailyRepository(instance())
    }
}