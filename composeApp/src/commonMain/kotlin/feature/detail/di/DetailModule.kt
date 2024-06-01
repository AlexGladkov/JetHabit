package feature.detail.di

import feature.detail.domain.DeleteHabitUseCase
import feature.detail.domain.GetDetailInfoUseCase
import feature.detail.domain.UpdateHabitUseCase
import org.kodein.di.*

val detailModule = DI.Module("detailModule") {
    bind<GetDetailInfoUseCase>() with provider {
        GetDetailInfoUseCase(instance())
    }
    
    bind<DeleteHabitUseCase>() with provider {
        DeleteHabitUseCase(instance())
    }
    
    bind<UpdateHabitUseCase>() with provider {
        UpdateHabitUseCase(instance())
    }
}