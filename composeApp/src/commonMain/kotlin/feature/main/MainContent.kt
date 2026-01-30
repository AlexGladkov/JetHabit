package feature.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import feature.chat.ui.ChatScreen
import feature.daily.presentation.DailyComponent
import feature.health.list.presentation.HealthComponent
import feature.profile.ProfileComponent
import feature.statistics.ui.StatisticsScreen
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.ic_chat
import tech.mobiledeveloper.jethabit.resources.ic_daily
import tech.mobiledeveloper.jethabit.resources.ic_health
import tech.mobiledeveloper.jethabit.resources.ic_profile
import tech.mobiledeveloper.jethabit.resources.ic_stats
import tech.mobiledeveloper.jethabit.resources.tab_chat
import tech.mobiledeveloper.jethabit.resources.tab_daily
import tech.mobiledeveloper.jethabit.resources.tab_health
import tech.mobiledeveloper.jethabit.resources.tab_profile
import tech.mobiledeveloper.jethabit.resources.tab_statistics

@Composable
fun MainContent(component: MainComponent) {
    val selectedTab by component.selectedTab.subscribeAsState()

    Scaffold(
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    selected = selectedTab == MainComponent.Tab.DAILY,
                    onClick = { component.onTabSelected(MainComponent.Tab.DAILY) },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_daily),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(Res.string.tab_daily)) }
                )
                BottomNavigationItem(
                    selected = selectedTab == MainComponent.Tab.HEALTH,
                    onClick = { component.onTabSelected(MainComponent.Tab.HEALTH) },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_health),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(Res.string.tab_health)) }
                )
                BottomNavigationItem(
                    selected = selectedTab == MainComponent.Tab.STATISTICS,
                    onClick = { component.onTabSelected(MainComponent.Tab.STATISTICS) },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_stats),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(Res.string.tab_statistics)) }
                )
                BottomNavigationItem(
                    selected = selectedTab == MainComponent.Tab.CHAT,
                    onClick = { component.onTabSelected(MainComponent.Tab.CHAT) },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_chat),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(Res.string.tab_chat)) }
                )
                BottomNavigationItem(
                    selected = selectedTab == MainComponent.Tab.PROFILE,
                    onClick = { component.onTabSelected(MainComponent.Tab.PROFILE) },
                    icon = {
                        Icon(
                            painter = painterResource(Res.drawable.ic_profile),
                            contentDescription = null
                        )
                    },
                    label = { Text(stringResource(Res.string.tab_profile)) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                MainComponent.Tab.DAILY -> {
                    val dailyStack by component.dailyStack.subscribeAsState()
                    Children(stack = dailyStack) { child ->
                        when (val instance = child.instance) {
                            is DailyComponent.Child.ListChild -> {
                                // Render DailyListScreen with instance.component
                                // This will be implemented when updating the screens
                                Text("Daily List Screen - TODO: Add DailyListScreen here")
                            }
                            is DailyComponent.Child.DetailChild -> {
                                // Render DetailScreen with instance.component
                                Text("Detail Screen - TODO: Add DetailScreen here")
                            }
                        }
                    }
                }
                MainComponent.Tab.HEALTH -> {
                    val healthStack by component.healthStack.subscribeAsState()
                    Children(stack = healthStack) { child ->
                        when (val instance = child.instance) {
                            is HealthComponent.Child.ListChild -> {
                                // Render HealthListScreen with instance.component
                                Text("Health List Screen - TODO: Add HealthListScreen here")
                            }
                            is HealthComponent.Child.TrackChild -> {
                                // Render TrackHabitScreen with instance.component
                                Text("Track Habit Screen - TODO: Add TrackHabitScreen here")
                            }
                            is HealthComponent.Child.CreateChild -> {
                                // Render CreateHabitScreen with instance.component
                                Text("Create Habit Screen - TODO: Add CreateHabitScreen here")
                            }
                        }
                    }
                }
                MainComponent.Tab.STATISTICS -> {
                    StatisticsScreen(component = component.statisticsComponent)
                }
                MainComponent.Tab.CHAT -> {
                    ChatScreen(component = component.chatComponent)
                }
                MainComponent.Tab.PROFILE -> {
                    val profileStack by component.profileStack.subscribeAsState()
                    Children(stack = profileStack) { child ->
                        when (val instance = child.instance) {
                            is ProfileComponent.Child.StartChild -> {
                                // Render ProfileStartScreen with instance.component
                                Text("Profile Start Screen - TODO: Add ProfileStartScreen here")
                            }
                            is ProfileComponent.Child.SettingsChild -> {
                                // Render SettingsScreen with instance.component
                                Text("Settings Screen - TODO: Add SettingsScreen here")
                            }
                            is ProfileComponent.Child.EditProfileChild -> {
                                // Render EditProfileScreen with instance.component
                                Text("Edit Profile Screen - TODO: Add EditProfileScreen here")
                            }
                            is ProfileComponent.Child.ProjectsChild -> {
                                // Render ProjectListScreen with instance.component
                                Text("Project List Screen - TODO: Add ProjectListScreen here")
                            }
                        }
                    }
                }
            }
        }
    }
}
