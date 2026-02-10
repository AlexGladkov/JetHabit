package feature.profile.start.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import feature.profile.start.presentation.ProfileStartComponent
import feature.profile.start.ui.views.ProfileView

@Composable
internal fun ProfileScreen(
    component: ProfileStartComponent
) {
    val viewState by component.state.subscribeAsState()

    ProfileView(viewState = viewState) {
        component.onEvent(it)
    }
} 