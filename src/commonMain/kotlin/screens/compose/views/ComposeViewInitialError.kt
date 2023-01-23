package screens.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.alexgladkov.jetpackcomposedemo.AppRes
import screens.compose.models.ComposeError
import ui.themes.JetHabitTheme

@Composable
internal fun ComposeViewInitialError(error: ComposeError) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(16.dp).align(Alignment.Center),
            text = when (error) {
                ComposeError.SendingGeneric -> AppRes.string.error_new_habit
            },
            color = JetHabitTheme.colors.errorColor
        )
    }
}

//@Preview
//@Composable
//fun ComposeViewInitialErrorView_Preview() {
//    MainTheme(darkTheme = true) {
//        ComposeViewInitialError(error = ComposeError.SendingGeneric)
//    }
//}