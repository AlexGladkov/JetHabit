package ru.alexgladkov.jetpackcomposedemo.screens.daily

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ru.alexgladkov.jetpackcomposedemo.R
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabbitCardItem
import ru.alexgladkov.jetpackcomposedemo.screens.daily.views.HabbitCardItemModel
import ru.alexgladkov.jetpackcomposedemo.ui.themes.JetHabbitTheme
import ru.alexgladkov.jetpackcomposedemo.ui.themes.MainTheme

@ExperimentalFoundationApi
@Composable
fun DailyScreen(
    navController: NavController,
    dailyViewModel: DailyViewModel = viewModel()
) {
    val exampleData = listOf(
        HabbitCardItemModel("Чистить зубы", isChecked = false),
        HabbitCardItemModel("Не есть сладкое", isChecked = false),
        HabbitCardItemModel("Не курить", isChecked = true)
    )

    Surface(color = JetHabbitTheme.colors.primaryBackground, modifier = Modifier.fillMaxSize()) {
        Box {
            LazyColumn(
                Modifier.background(JetHabbitTheme.colors.primaryBackground),
                content = {
                    stickyHeader {
                        Text(
                            modifier = Modifier.padding(
                                horizontal = JetHabbitTheme.shapes.padding,
                                vertical = JetHabbitTheme.shapes.padding + 8.dp),
                            text = stringResource(id = R.string.days_today),
                            style = JetHabbitTheme.typography.heading,
                            color = JetHabbitTheme.colors.primaryText
                        )
                    }

                    exampleData.forEach {
                        item {
                            HabbitCardItem(model = it)
                        }
                    }
                })

            FloatingActionButton(
                modifier = Modifier
                    .align(BottomEnd)
                    .padding(JetHabbitTheme.shapes.padding),
                backgroundColor = JetHabbitTheme.colors.tintColor,
                onClick = {
                    navController.navigate("settings")
                }) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_settings_24),
                    contentDescription = "Settings icon",
                    tint = White
                )
            }
        }
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DailyScreen_Preview() {
    MainTheme(darkTheme = false) {
        DailyScreen(
            navController = rememberNavController()
        )
    }
}

@ExperimentalFoundationApi
@Preview(showBackground = true)
@Composable
fun DailyScreenDark_Preview() {
    MainTheme(darkTheme = true) {
        DailyScreen(
            navController = rememberNavController()
        )
    }
}

