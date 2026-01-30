package feature.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.MutableValue
import feature.chat.presentation.ChatComponent
import feature.daily.presentation.DailyComponent
import feature.health.list.presentation.HealthComponent
import feature.profile.ProfileComponent
import feature.statistics.presentation.StatisticsComponent
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.kodein.di.DIAware

class MainComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onCreateHabit: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val _selectedTab = MutableValue(Tab.DAILY)
    val selectedTab: Value<Tab> = _selectedTab

    private val dailyNavigation = StackNavigation<DailyComponent.Config>()
    private val healthNavigation = StackNavigation<HealthComponent.Config>()
    private val profileNavigation = StackNavigation<ProfileComponent.Config>()

    val dailyStack: Value<ChildStack<*, DailyComponent.Child>> =
        childStack(
            source = dailyNavigation,
            serializer = DailyComponent.Config.serializer(),
            initialConfiguration = DailyComponent.Config.List,
            handleBackButton = true,
            childFactory = { config, childContext ->
                DailyComponent(
                    componentContext = childContext,
                    di = di,
                    config = config,
                    navigation = dailyNavigation,
                    onCreateHabit = onCreateHabit
                ).child(config)
            },
            key = "daily_stack"
        )

    val healthStack: Value<ChildStack<*, HealthComponent.Child>> =
        childStack(
            source = healthNavigation,
            serializer = HealthComponent.Config.serializer(),
            initialConfiguration = HealthComponent.Config.List,
            handleBackButton = true,
            childFactory = { config, childContext ->
                HealthComponent(
                    componentContext = childContext,
                    di = di,
                    config = config,
                    navigation = healthNavigation,
                    onCreateHabit = onCreateHabit
                ).child(config)
            },
            key = "health_stack"
        )

    val statisticsComponent: StatisticsComponent =
        StatisticsComponent(
            componentContext = componentContext,
            di = di
        )

    val chatComponent: ChatComponent =
        ChatComponent(
            componentContext = componentContext,
            di = di
        )

    val profileStack: Value<ChildStack<*, ProfileComponent.Child>> =
        childStack(
            source = profileNavigation,
            serializer = ProfileComponent.Config.serializer(),
            initialConfiguration = ProfileComponent.Config.Start,
            handleBackButton = true,
            childFactory = { config, childContext ->
                ProfileComponent(
                    componentContext = childContext,
                    di = di,
                    config = config,
                    navigation = profileNavigation
                ).child(config)
            },
            key = "profile_stack"
        )

    fun onTabSelected(tab: Tab) {
        _selectedTab.value = tab
    }

    enum class Tab {
        DAILY, HEALTH, STATISTICS, CHAT, PROFILE
    }
}
