package data.features.medication

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val medicationModule = DI.Module("medicationModule") {
    bind<MedicationRepository>() with provider {
        MedicationRepository(instance())
    }
}