package screens.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import di.LocalPlatform
import di.Platform
import tech.mobiledeveloper.shared.AppRes
import ui.themes.JetHabitTheme

@Composable
internal fun ComposeViewSuccess(modifier: Modifier = Modifier, onCloseClick: () -> Unit) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = JetHabitTheme.colors.primaryBackground
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center),
                horizontalAlignment = CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier
                        .size(90.dp)
                        .align(CenterHorizontally),
                    imageVector = Icons.Filled.CheckCircle,
                    tint = JetHabitTheme.colors.controlColor,
                    contentDescription = "Accepted Icon"
                )

                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = AppRes.string.compose_success_add,
                    style = JetHabitTheme.typography.body,
                    color = JetHabitTheme.colors.primaryText
                )

                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .height(48.dp)
                        .fillMaxWidth(),
                    onClick = onCloseClick,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = JetHabitTheme.colors.controlColor,
                        disabledBackgroundColor = JetHabitTheme.colors.controlColor.copy(
                            alpha = 0.3f
                        )
                    )
                ) {
                    Text(
                        text = AppRes.string.action_close,
                        style = JetHabitTheme.typography.body,
                        color = Color.White
                    )
                }
            }
        }
    }
}

//@Composable
//@Preview
//fun ComposeViewSuccess_Preview() {
//    MainTheme(darkTheme = true) {
//        ComposeViewSuccess(onCloseClick = {})
//    }
//}