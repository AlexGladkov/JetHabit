package ru.alexgladkov.jetpackcomposedemo.screens.main

import androidx.annotation.StringRes
import ru.alexgladkov.jetpackcomposedemo.R

sealed class MainBottomScreen(val route: String, @StringRes val resourceId: Int) {
    object Daily : MainBottomScreen("daily", R.string.title_daily)
    object Settings : MainBottomScreen("settings", R.string.title_settings)
}
