package feature.daily.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.kodein.di.DIAware

class DailyComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val config: Config,
    private val navigation: StackNavigation<Config>,
    private val onCreateHabit: () -> Unit
) : ComponentContext by componentContext, DIAware {

    @Serializable
    sealed interface Config {
        @Serializable
        data object List : Config

        @Serializable
        data class Detail(val habitId: String) : Config
    }

    sealed interface Child {
        data class ListChild(val component: DailyListComponent) : Child
        data class DetailChild(val component: DetailComponent) : Child
    }

    fun child(config: Config): Child {
        return when (config) {
            is Config.List -> Child.ListChild(
                DailyListComponent(
                    componentContext = this,
                    di = di,
                    onHabitSelected = { habitId ->
                        navigation.push(Config.Detail(habitId))
                    },
                    onComposeClicked = onCreateHabit
                )
            )
            is Config.Detail -> Child.DetailChild(
                DetailComponent(
                    componentContext = this,
                    di = di,
                    habitId = config.habitId,
                    onNavigateBack = {
                        navigation.pop()
                    }
                )
            )
        }
    }
}
