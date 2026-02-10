package feature.health.list.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import feature.create.presentation.CreateHabitComponent
import feature.health.track.presentation.TrackHabitComponent
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.kodein.di.DIAware

class HealthComponent(
    componentContext: ComponentContext,
    override val di: DI,
    config: Config,
    private val navigation: StackNavigation<Config>,
    private val onCreateHabit: () -> Unit
) : ComponentContext by componentContext, DIAware {

    @Serializable
    sealed interface Config {
        @Serializable
        data object List : Config

        @Serializable
        data class Track(val habitId: String) : Config

        @Serializable
        data class Create(val type: String?) : Config
    }

    sealed interface Child {
        data class ListChild(val component: HealthListComponent) : Child
        data class TrackChild(val component: TrackHabitComponent) : Child
        data class CreateChild(val component: CreateHabitComponent) : Child
    }

    fun child(config: Config): Child {
        return when (config) {
            is Config.List -> Child.ListChild(
                HealthListComponent(
                    componentContext = childContext("list"),
                    di = di,
                    onHabitSelected = { habitId ->
                        navigation.push(Config.Track(habitId))
                    },
                    onCreateClicked = {
                        onCreateHabit()
                    }
                )
            )
            is Config.Track -> Child.TrackChild(
                TrackHabitComponent(
                    componentContext = childContext("track_${config.habitId}"),
                    di = di,
                    habitId = config.habitId,
                    onNavigateBack = {
                        navigation.pop()
                    }
                )
            )
            is Config.Create -> Child.CreateChild(
                CreateHabitComponent(
                    componentContext = childContext("create_${config.type}"),
                    di = di,
                    habitType = config.type,
                    onNavigateBack = {
                        navigation.pop()
                    }
                )
            )
        }
    }
}
