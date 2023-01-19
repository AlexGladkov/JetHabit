package screens.compose.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import screens.compose.models.ComposeViewState

@ExperimentalFoundationApi
@Composable
fun ComposeViewInitial(
    modifier: Modifier = Modifier,
    state: ComposeViewState.ViewStateInitial,
    onCheckedChange: (Boolean) -> Unit,
    onTitleChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
) {
//    Surface(modifier = modifier.fillMaxSize(), color = JetHabitTheme.colors.primaryBackground) {
//        Box {
//            LazyColumn(
//                Modifier.background(JetHabitTheme.colors.primaryBackground),
//                content = {
//                    stickyHeader {
//                        Text(
//                            modifier = Modifier.padding(
//                                horizontal = JetHabitTheme.shapes.padding,
//                                vertical = JetHabitTheme.shapes.padding + 8.dp
//                            ),
//                            text = stringResource(id = R.string.compose_new_record),
//                            style = JetHabitTheme.typography.heading,
//                            color = JetHabitTheme.colors.primaryText
//                        )
//                    }
//
//                    item {
//                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
//                            Text(
//                                text = stringResource(id = R.string.compose_title),
//                                style = JetHabitTheme.typography.caption,
//                                color = JetHabitTheme.colors.secondaryText
//                            )
//
//                            TextField(
//                                modifier = Modifier
//                                    .padding(top = 4.dp)
//                                    .fillMaxWidth(),
//                                singleLine = true,
//                                enabled = !state.isSending,
//                                value = state.habitTitle,
//                                onValueChange = onTitleChanged,
//                                colors = TextFieldDefaults.textFieldColors(
//                                    backgroundColor = JetHabitTheme.colors.primaryBackground,
//                                    textColor = JetHabitTheme.colors.primaryText,
//                                    focusedIndicatorColor = JetHabitTheme.colors.tintColor,
//                                    disabledIndicatorColor = JetHabitTheme.colors.controlColor,
//                                    cursorColor = JetHabitTheme.colors.tintColor
//                                )
//                            )
//                        }
//                    }
//
//                    item {
//                        Row(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
//                            Text(
//                                modifier = Modifier.padding(end = 16.dp),
//                                text = stringResource(id = R.string.compose_is_good),
//                                style = JetHabitTheme.typography.body,
//                                color = JetHabitTheme.colors.primaryText
//                            )
//
//                            Checkbox(
//                                checked = state.isGoodHabit,
//                                enabled = !state.isSending,
//                                onCheckedChange = onCheckedChange,
//                                colors = CheckboxDefaults.colors(
//                                    checkedColor = JetHabitTheme.colors.tintColor,
//                                    uncheckedColor = JetHabitTheme.colors.secondaryText,
//                                    disabledColor = JetHabitTheme.colors.tintColor.copy(
//                                        alpha = 0.3f
//                                    )
//                                )
//                            )
//                        }
//                    }
//
//                    item {
//                        Button(
//                            modifier = Modifier
//                                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
//                                .height(48.dp)
//                                .fillMaxWidth(),
//                            onClick = onSaveClicked,
//                            enabled = !state.isSending,
//                            colors = ButtonDefaults.buttonColors(
//                                backgroundColor = JetHabitTheme.colors.tintColor,
//                                disabledBackgroundColor = JetHabitTheme.colors.tintColor.copy(
//                                    alpha = 0.3f
//                                )
//                            )
//                        ) {
//                            if (state.isSending) {
//                                CircularProgressIndicator(
//                                    modifier = Modifier.size(20.dp),
//                                    color = Color.White,
//                                    strokeWidth = 2.dp
//                                )
//                            } else {
//                                Text(
//                                    text = stringResource(id = R.string.action_add),
//                                    style = JetHabitTheme.typography.body,
//                                    color = Color.White
//                                )
//                            }
//                        }
//                    }
//
//                    state.sendingError?.let { error ->
//                        item {
//                            ComposeViewInitialError(error = error)
//                        }
//                    }
//                })
//        }
//    }
}

//@ExperimentalFoundationApi
//@Preview
//@Composable
//fun ComposeViewInitial_Preview() {
//    MainTheme(darkTheme = true) {
//        ComposeViewInitial(
//            state = ComposeViewState.ViewStateInitial(),
//            onCheckedChange = {},
//            onTitleChanged = {},
//            onSaveClicked = {}
//        )
//    }
//}

//@ExperimentalFoundationApi
//@Preview
//@Composable
//fun ComposeViewInitialFilled_Preview() {
//    MainTheme(darkTheme = true) {
//        ComposeViewInitial(
//            state = ComposeViewState
//                .ViewStateInitial(
//                    habitTitle = "Test habbit",
//                    isGoodHabit = false,
//                    sendingError = ComposeError.SendingGeneric,
//                    isSending = true
//                ),
//            onCheckedChange = {},
//            onTitleChanged = {},
//            onSaveClicked = {}
//        )
//    }
//}