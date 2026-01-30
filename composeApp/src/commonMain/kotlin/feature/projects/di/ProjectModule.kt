package feature.projects.di

import feature.projects.domain.CreateProjectUseCase
import feature.projects.domain.DeleteProjectUseCase
import feature.projects.domain.GetAllProjectsUseCase
import feature.projects.domain.UpdateProjectUseCase
import org.koin.dsl.module

val projectModule = module {
    factory<CreateProjectUseCase> { CreateProjectUseCase(get()) }
    factory<GetAllProjectsUseCase> { GetAllProjectsUseCase(get()) }
    factory<UpdateProjectUseCase> { UpdateProjectUseCase(get()) }
    factory<DeleteProjectUseCase> { DeleteProjectUseCase(get(), get()) }
}
