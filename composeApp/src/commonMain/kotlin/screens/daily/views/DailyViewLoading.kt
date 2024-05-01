package screens.daily.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ui.themes.JetHabitTheme

@Composable
internal fun DailyViewLoading() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(JetHabitTheme.colors.primaryBackground)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = JetHabitTheme.colors.tintColor
        )
    }
}

//@Preview
//@Composable
//fun DailyViewLoading_Preview() {
//    MainTheme(darkTheme = true) {
//        DailyViewLoading()
//    }
//}