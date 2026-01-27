package feature.projects.di

import feature.projects.domain.CreateProjectUseCase
import feature.projects.domain.DeleteProjectUseCase
import feature.projects.domain.GetAllProjectsUseCase
import feature.projects.domain.UpdateProjectUseCase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.provider

val projectModule = DI.Module("projectModule") {
    bind<CreateProjectUseCase>() with provider { CreateProjectUseCase(instance()) }
    bind<GetAllProjectsUseCase>() with provider { GetAllProjectsUseCase(instance()) }
    bind<UpdateProjectUseCase>() with provider { UpdateProjectUseCase(instance()) }
    bind<DeleteProjectUseCase>() with provider { DeleteProjectUseCase(instance(), instance()) }
}
