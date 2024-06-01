package feature.daily.ui.models

import screens.daily.views.HabitCardItemModel
import utils.Weekday

data class DailyViewState(
    val currentDay: Weekday = Weekday.Today,
    val hasNextDay: Boolean = false,
    val habits: List<HabitCardItemModel> = emptyList()
)