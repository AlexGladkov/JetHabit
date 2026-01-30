package feature.detail.di

import feature.detail.domain.DeleteHabitUseCase
import feature.detail.domain.GetDetailInfoUseCase
import feature.detail.domain.UpdateHabitUseCase
import org.koin.dsl.module

val detailModule = module {
    factory<GetDetailInfoUseCase> {
        GetDetailInfoUseCase(get())
    }

    factory<DeleteHabitUseCase> {
        DeleteHabitUseCase(get())
    }

    factory<UpdateHabitUseCase> {
        UpdateHabitUseCase(get())
    }
}