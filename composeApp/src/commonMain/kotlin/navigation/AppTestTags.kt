package navigation

/** Stable Compose semantics tags used by smoke tests. */
object AppTestTags {
    const val BottomNavigation: String = "bottom_navigation"

    /** Returns a stable tag for a bottom navigation item by route. */
    fun bottomNavigationItem(route: String): String = "bottom_navigation_item_$route"

    // E7: habit heatmap in Statistics
    const val HeatmapSection: String = "heatmap_section"
    const val HeatmapLegend: String = "heatmap_legend"
    const val HeatmapEmptyState: String = "heatmap_empty_state"
    const val DayDetailsDialog: String = "day_details_dialog"
    const val DayDetailsDismiss: String = "day_details_dismiss"

    /** Stable tag for a heatmap cell by flat index (row-major over the 7x12 grid). */
    fun heatmapCell(index: Int): String = "heatmap_cell_$index"
}
