package feature.daily.ui.models

import screens.daily.views.HabitCardItemModel
import tech.mobiledeveloper.jethabit.app.AppRes

data class DailyViewState(
    val currentDay: String = AppRes.string.days_today,
    val hasNextDay: Boolean = false,
    val habits: List<HabitCardItemModel> = emptyList()
)