package feature.profile

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import feature.profile.edit.presentation.EditProfileComponent
import feature.profile.start.presentation.ProfileStartComponent
import feature.projects.presentation.ProjectListComponent
import feature.settings.presentation.SettingsComponent
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.kodein.di.DIAware

class ProfileComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val config: Config,
    private val navigation: StackNavigation<Config>
) : ComponentContext by componentContext, DIAware {

    @Serializable
    sealed interface Config {
        @Serializable
        data object Start : Config

        @Serializable
        data object Settings : Config

        @Serializable
        data object EditProfile : Config

        @Serializable
        data object Projects : Config
    }

    sealed interface Child {
        data class StartChild(val component: ProfileStartComponent) : Child
        data class SettingsChild(val component: SettingsComponent) : Child
        data class EditProfileChild(val component: EditProfileComponent) : Child
        data class ProjectsChild(val component: ProjectListComponent) : Child
    }

    fun child(config: Config): Child {
        return when (config) {
            is Config.Start -> Child.StartChild(
                ProfileStartComponent(
                    componentContext = this,
                    di = di,
                    onNavigateToEdit = {
                        navigation.push(Config.EditProfile)
                    },
                    onNavigateToSettings = {
                        navigation.push(Config.Settings)
                    },
                    onNavigateToProjects = {
                        navigation.push(Config.Projects)
                    }
                )
            )
            is Config.Settings -> Child.SettingsChild(
                SettingsComponent(
                    componentContext = this,
                    di = di,
                    onNavigateBack = {
                        navigation.pop()
                    }
                )
            )
            is Config.EditProfile -> Child.EditProfileChild(
                EditProfileComponent(
                    componentContext = this,
                    di = di,
                    onNavigateBack = {
                        navigation.pop()
                    }
                )
            )
            is Config.Projects -> Child.ProjectsChild(
                ProjectListComponent(
                    componentContext = this,
                    di = di,
                    onNavigateBack = {
                        navigation.pop()
                    }
                )
            )
        }
    }
}
