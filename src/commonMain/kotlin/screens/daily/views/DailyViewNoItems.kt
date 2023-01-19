package ru.alexgladkov.jetpackcomposedemo.screens.daily.views

import androidx.compose.runtime.Composable

@Composable
fun DailyViewNoItems(
    onComposeClick: () -> Unit
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
//                    imageVector = Icons.Filled.Info,
//                    tint = JetHabitTheme.colors.controlColor,
//                    contentDescription = "No Items Found"
//                )
//
//                Text(
//                    modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
//                    text = stringResource(id = R.string.daily_no_items),
//                    style = JetHabitTheme.typography.body,
//                    color = JetHabitTheme.colors.primaryText,
//                    textAlign = TextAlign.Center
//                )
//
//                JetHabitButton(
//                    modifier = Modifier.fillMaxWidth(),
//                    backgroundColor = JetHabitTheme.colors.controlColor,
//                    text = stringResource(id = R.string.action_add),
//                    onClick = onComposeClick
//                )
//            }
//        }
//    }
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