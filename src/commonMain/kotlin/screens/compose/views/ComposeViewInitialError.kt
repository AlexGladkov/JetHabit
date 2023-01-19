package screens.compose.views

import androidx.compose.runtime.Composable
import screens.compose.models.ComposeError

@Composable
fun ComposeViewInitialError(error: ComposeError) {
//    Box(modifier = Modifier.fillMaxWidth()) {
//        Text(
//            modifier = Modifier.padding(16.dp).align(Alignment.Center),
//            text = when (error) {
//                ComposeError.SendingGeneric -> stringResource(id = R.string.error_new_habit)
//            },
//            color = JetHabitTheme.colors.errorColor
//        )
//    }
}

//@Preview
//@Composable
//fun ComposeViewInitialErrorView_Preview() {
//    MainTheme(darkTheme = true) {
//        ComposeViewInitialError(error = ComposeError.SendingGeneric)
//    }
//}