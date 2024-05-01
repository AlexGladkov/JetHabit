package screens.stats.models

import screens.stats.views.StatisticCellModel

data class StatsViewState(
    val activeProgress: List<StatisticCellModel> = emptyList(),
    val pastActivities: List<StatisticCellModel> = emptyList()
)