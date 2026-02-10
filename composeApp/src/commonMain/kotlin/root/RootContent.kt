package root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import feature.create.ui.CreateHabitFlowScreen
import feature.main.MainContent
import screens.splash.SplashScreen

@Composable
fun RootContent(component: RootComponent) {
    Children(
        stack = component.stack,
        animation = stackAnimation(fade())
    ) {
        when (val child = it.instance) {
            is RootComponent.Child.Splash -> SplashScreen(child.component)
            is RootComponent.Child.Main -> MainContent(child.component)
            is RootComponent.Child.Create -> CreateHabitFlowScreen(child.component)
        }
    }
}
