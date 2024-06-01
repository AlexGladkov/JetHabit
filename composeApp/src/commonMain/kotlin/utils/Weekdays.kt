package utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.stringResource
import tech.mobiledeveloper.jethabit.resources.Res
import tech.mobiledeveloper.jethabit.resources.days_today
import tech.mobiledeveloper.jethabit.resources.days_yesterday

sealed class Weekday {
    data object Today : Weekday()
    data object Yesterday : Weekday()
    class Custom(val value: String) : Weekday()
}

@Composable
fun Weekday.title(): String = when (this) {
    is Weekday.Custom -> value
    Weekday.Today -> stringResource(Res.string.days_today)
    Weekday.Yesterday -> stringResource(Res.string.days_yesterday)
}