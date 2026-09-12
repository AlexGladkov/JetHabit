package feature.statistics.ui.models

import androidx.compose.runtime.Stable
import feature.daily.domain.HeatmapDayDetails
import feature.daily.domain.HeatmapModel
import feature.projects.data.ProjectEntity
import screens.stats.models.HabitStatistics

@Stable
data class StatisticsViewState(
    val isLoading: Boolean = false,
    val hasData: Boolean = false,
    val statistics: List<HabitStatistics> = emptyList(),
    val projects: List<ProjectEntity> = emptyList(),
    val selectedProjectId: String? = null,
    val isUncategorizedSelected: Boolean = false,
    // E7: habit heatmap
    val heatmap: HeatmapModel? = null,
    val selectedDayDetails: HeatmapDayDetails? = null
)
