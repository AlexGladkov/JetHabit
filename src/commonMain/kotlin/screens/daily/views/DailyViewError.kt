package ru.alexgladkov.jetpackcomposedemo.screens.daily.views

import androidx.compose.runtime.Composable

@Composable
fun DailyViewError(
    onReloadClick: () -> Unit
) {
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = JetHabitTheme.colors.primaryBackground
//    ) {
//        Box(modifier = Modifier.fillMaxSize()) {
//            Column(
//                modifier = Modifier.padding(16.dp).align(Alignment.Center),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    modifier = Modifier.size(96.dp),
//                    imageVector = Icons.Filled.Warning,
//                    tint = JetHabitTheme.colors.controlColor,
//                    contentDescription = "Error loading items"
//                )
//
//                Text(
//                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
//                    text = stringResource(id = R.string.daily_error_loading),
//                    style = JetHabitTheme.typography.body,
//                    color = JetHabitTheme.colors.primaryText,
//                    textAlign = TextAlign.Center
//                )
//
//                JetHabitButton(
//                    modifier = Modifier.fillMaxWidth(),
//                    backgroundColor = JetHabitTheme.colors.controlColor,
//                    text = stringResource(id = R.string.action_refresh),
//                    onClick = onReloadClick
//                )
//            }
//        }
//    }
}

//@Composable
//@Preview
//fun DailyViewError_Preview() {
//    MainTheme(darkTheme = true) {
//       DailyViewError(onReloadClick = {})
//    }
//}