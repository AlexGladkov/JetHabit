package screens.daily.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import di.LocalPlatform
import di.Platform
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitTheme
import ui.themes.components.JetHabitButton

@Composable
internal fun DailyViewNoItems(
    onComposeClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp).align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(96.dp),
                    imageVector = Icons.Filled.Info,
                    tint = JetHabitTheme.colors.controlColor,
                    contentDescription = "No Items Found"
                )

                Text(
                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
                    text = AppRes.string.daily_no_items,
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.primaryText,
                    textAlign = TextAlign.Center
                )

                JetHabitButton(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = JetHabitTheme.colors.controlColor,
                    text = AppRes.string.action_add,
                    onClick = onComposeClick
                )
            }
        }
    }
}

//@Preview
//@Composable
//fun DailyViewNoItem_Preview() {
//    MainTheme(darkTheme = true) {
//        DailyViewNoItems(
//            onComposeClick = {}
//        )
//    }
//}