package root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import feature.create.presentation.CreateHabitComponent
import feature.main.MainComponent
import feature.splash.SplashComponent
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.kodein.di.DIAware

class RootComponent(
    componentContext: ComponentContext,
    override val di: DI
) : ComponentContext by componentContext, DIAware {

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.Splash,
            handleBackButton = true,
            childFactory = ::child
        )

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Splash -> Child.Splash(
                component = SplashComponent(
                    componentContext = componentContext,
                    di = di,
                    onFinished = { navigation.push(Config.Main) }
                )
            )
            is Config.Main -> Child.Main(
                component = MainComponent(
                    componentContext = componentContext,
                    di = di,
                    onCreateHabit = { navigation.push(Config.Create) }
                )
            )
            is Config.Create -> Child.Create(
                component = CreateHabitComponent(
                    componentContext = componentContext,
                    di = di,
                    onFinished = { navigation.pop() }
                )
            )
        }

    sealed class Child {
        class Splash(val component: SplashComponent) : Child()
        class Main(val component: MainComponent) : Child()
        class Create(val component: CreateHabitComponent) : Child()
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Splash : Config()

        @Serializable
        data object Main : Config()

        @Serializable
        data object Create : Config()
    }
}
