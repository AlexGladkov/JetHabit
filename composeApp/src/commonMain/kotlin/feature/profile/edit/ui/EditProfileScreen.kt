package feature.profile.edit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import feature.profile.edit.presentation.EditProfileComponent

@Composable
internal fun EditProfileScreen(
    component: EditProfileComponent
) {
    val viewState by component.state.subscribeAsState()

    EditProfileView(viewState = viewState) {
        component.onEvent(it)
    }
} 