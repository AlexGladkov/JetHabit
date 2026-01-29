# Specification: Add Weekly Statistics Screen

## Summary

Add a weekly statistics view to the existing Statistics tab in JetHabit. The current Statistics screen shows all-time habit completion data as colored square grids. This feature adds a tab-based UI ("Weekly" / "All Time") within the Statistics screen, where the "Weekly" tab shows habit completion statistics scoped to a single week with week-by-week navigation, bar chart visualization, and per-day breakdown.

## Requirements

### Interview Notes

The MCP `ask_user` tool was unavailable during the interview phase. Requirements were derived from:
- The task description: "Show weekly habit completion statistics with charts and progress indicators"
- Thorough codebase analysis of existing patterns, data models, and UI conventions
- Alignment with the app's existing architecture (BaseViewModel MVI pattern, Compose UI, Room database, Kodein DI)

### Functional Requirements

1. **Tab-based layout**: Add two tabs ("Weekly" / "All Time") at the top of the Statistics screen. "Weekly" is the new view; "All Time" retains the current behavior.
2. **Week navigation**: The weekly view displays data for a selected week (Mon–Sun). Left/right arrows or swipe gestures allow navigating between weeks. A label shows the date range (e.g., "20.01 – 26.01.2025").
3. **Bar chart visualization**: A horizontal or vertical bar chart shows the number of habits completed per day of the week (Mon, Tue, Wed, Thu, Fri, Sat, Sun). Bars are colored using `JetHabitTheme.colors.tintColor`. The chart is rendered using Compose Canvas (consistent with existing `HabitTrackingChart`).
4. **Weekly completion summary**: Display an overall weekly completion rate (percentage) and the count of completed vs total habit check-ins for the week.
5. **Per-habit breakdown**: Below the chart, list each habit with its weekly completion status (7 day indicators for Mon–Sun, showing checked/unchecked state). Reuse the existing color scheme: `tintColor` for checked, `errorColor` at 20% opacity for unchecked-but-tracked, `errorColor` at 5% for never-tracked.
6. **Progress indicator**: A circular or linear progress indicator showing the overall weekly completion percentage.
7. **Empty state**: If no habits exist, show the existing `StatisticsViewNoItems` empty state view.
8. **Current week default**: The view defaults to the current week on initial load.
9. **Respect `daysToCheck`**: Only count days that the habit is configured to be tracked on (from `HabitEntity.daysToCheck`).

### Non-Functional Requirements

- Follow existing MVI architecture (`BaseViewModel<State, Action, Event>`)
- Use `kotlinx.datetime` for all date calculations (cross-platform)
- No third-party charting libraries; use Compose Canvas API (consistent with existing charts)
- Maintain existing theme system (`JetHabitTheme`)

## Files to Create

| File | Purpose |
|------|---------|
| `composeApp/src/commonMain/kotlin/feature/statistics/ui/views/WeeklyStatisticsView.kt` | Main weekly statistics composable with bar chart and week navigation |
| `composeApp/src/commonMain/kotlin/feature/statistics/ui/views/WeeklyBarChart.kt` | Canvas-based bar chart showing daily completions for the week |
| `composeApp/src/commonMain/kotlin/feature/statistics/ui/views/WeeklyHabitItem.kt` | Per-habit row showing 7-day completion indicators |
| `composeApp/src/commonMain/kotlin/feature/statistics/ui/views/WeeklyProgressIndicator.kt` | Circular progress indicator for weekly completion rate |

## Files to Modify

| File | Change |
|------|--------|
| `composeApp/src/commonMain/kotlin/feature/statistics/ui/StatisticsScreen.kt` | Add tab bar (Weekly / All Time) and conditional rendering |
| `composeApp/src/commonMain/kotlin/feature/statistics/ui/models/StatisticsViewState.kt` | Add weekly-specific state fields (selected week, weekly stats data, active tab) |
| `composeApp/src/commonMain/kotlin/feature/statistics/ui/models/StatisticsEvent.kt` | Add events: `SelectWeek`, `NextWeek`, `PreviousWeek`, `SwitchTab` |
| `composeApp/src/commonMain/kotlin/feature/statistics/presentation/StatisticsViewModel.kt` | Add weekly statistics calculation logic, week navigation handling |

## Detailed Implementation Approach

### 1. State Model Changes (`StatisticsViewState.kt`)

Add an enum for the active tab and weekly-specific data:

```kotlin
enum class StatisticsTab { WEEKLY, ALL_TIME }

data class WeeklyHabitStat(
    val habitId: String,
    val habitTitle: String,
    val dailyStatus: List<DayStatus> // 7 entries, Mon-Sun
)

data class DayStatus(
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val isChecked: Boolean,
    val isApplicable: Boolean // whether habit is scheduled for this day
)

data class WeeklyData(
    val weekStart: LocalDate, // Monday
    val weekEnd: LocalDate,   // Sunday
    val completedCount: Int,
    val totalCount: Int,
    val completionRate: Float,
    val dailyCompletionCounts: List<Int>, // 7 entries (completed habits per day)
    val dailyTotalCounts: List<Int>,      // 7 entries (total applicable habits per day)
    val habitStats: List<WeeklyHabitStat>
)
```

Extend `StatisticsViewState`:
```kotlin
data class StatisticsViewState(
    val isLoading: Boolean = false,
    val hasData: Boolean = false,
    val statistics: List<HabitStatistics> = emptyList(),
    val activeTab: StatisticsTab = StatisticsTab.WEEKLY,
    val weeklyData: WeeklyData? = null
)
```

### 2. Event Changes (`StatisticsEvent.kt`)

```kotlin
sealed class StatisticsEvent {
    object LoadStatistics : StatisticsEvent()
    object NextWeek : StatisticsEvent()
    object PreviousWeek : StatisticsEvent()
    data class SwitchTab(val tab: StatisticsTab) : StatisticsEvent()
}
```

### 3. ViewModel Changes (`StatisticsViewModel.kt`)

Add `loadWeeklyStatistics(weekStart: LocalDate)` method:
- Calculate Monday of the selected week
- For each habit, check `daysToCheck` to determine applicable days
- Query `DailyDao.isHabitChecked()` for each applicable day
- Aggregate into `WeeklyData`

Handle `NextWeek`/`PreviousWeek` by adjusting the stored week start date by +/- 7 days and reloading.

Handle `SwitchTab` by updating `activeTab` in state.

### 4. Statistics Screen Changes (`StatisticsScreen.kt`)

Add a `TabRow` with two tabs ("Weekly", "All Time") below the title. Based on `activeTab`:
- `WEEKLY`: Render `WeeklyStatisticsView`
- `ALL_TIME`: Render the existing `LazyColumn` with `StatisticsItem` list

### 5. Weekly Statistics View (`WeeklyStatisticsView.kt`)

Layout (top to bottom):
1. **Week navigator**: Row with `<` button, date range text ("20.01 – 26.01"), `>` button
2. **Progress indicator**: `WeeklyProgressIndicator` showing completion rate
3. **Bar chart**: `WeeklyBarChart` showing per-day completion counts
4. **Habit list**: `LazyColumn` of `WeeklyHabitItem` cards

### 6. Bar Chart (`WeeklyBarChart.kt`)

Canvas-based vertical bar chart:
- 7 bars (Mon–Sun), labeled with day abbreviations
- Bar height proportional to completion count / total applicable habits for that day
- Filled with `tintColor`, background bar in `controlColor`
- Height: ~150.dp

### 7. Weekly Habit Item (`WeeklyHabitItem.kt`)

Card showing:
- Habit title
- Row of 7 day indicators (small colored squares, same color scheme as `StatisticsItem`)
- Day labels (M, T, W, T, F, S, S)
- Weekly completion rate for that habit

### 8. Progress Indicator (`WeeklyProgressIndicator.kt`)

Circular arc drawn with Canvas:
- Background circle in `controlColor`
- Foreground arc in `tintColor` proportional to completion rate
- Percentage text centered inside

## Acceptance Criteria

1. The Statistics tab shows two sub-tabs: "Weekly" and "All Time"
2. "All Time" tab displays the existing statistics view (no regressions)
3. "Weekly" tab defaults to the current week (Mon–Sun)
4. Users can navigate to previous/next weeks using arrow buttons
5. A bar chart shows the number of completed habits per day of the week
6. A progress indicator shows the overall weekly completion percentage
7. Each habit is listed with 7-day completion indicators
8. Days where a habit is not scheduled (per `daysToCheck`) are visually distinct (faded/not counted)
9. Empty state is shown when no habits exist
10. The view correctly handles habits with different `daysToCheck` configurations
11. Week boundaries are Monday–Sunday
12. The feature works on all supported platforms (KMP)

## Edge Cases and Risks

### Edge Cases
- **Habit created mid-week**: Only show data from the habit's `startDate` onward; days before should appear as not applicable
- **Habit ended mid-week**: Only show data up to `endDate`; days after should appear as not applicable
- **No habits at all**: Show `StatisticsViewNoItems` empty state
- **All habits are TRACKER type (not REGULAR)**: The current code only shows stats when regular habits exist; weekly view should follow the same rule
- **Week with no applicable days**: If all habits have `daysToCheck` that exclude every day in the week, show 0% completion
- **Future weeks**: Allow navigation but show no completion data for future dates
- **`daysToCheck` parsing**: The field stores a stringified list like `[0,1,2,3,4,5,6]`; parsing must handle brackets and whitespace (existing pattern in `StatisticsViewModel`)

### Risks
- **Performance**: Querying `DailyDao` per habit per day could be slow with many habits. The existing code already does this pattern, so it should be acceptable for weekly (7 days max). If performance is a concern, a batch query could be added to `DailyDao` later.
- **Date handling across timezones**: Using `TimeZone.currentSystemDefault()` (same as existing code) — consistent but could show different results if user changes timezone.

## Out of Scope

- Monthly or yearly statistics views (only weekly is in scope)
- Streak tracking or streak visualization
- Data export functionality
- Habit-specific detail drill-down from the weekly view
- Animations or transitions between weeks
- Localization of day names (will use short English abbreviations initially, consistent with existing patterns)
- Custom week start day (always Monday; configurable start day is out of scope)
- Push notifications or reminders based on weekly performance
