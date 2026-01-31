# Activity Feed — Habit Streak Tracking

## Summary

Add an Activity Feed feature that displays habit streak milestones and streak-broken notifications. The feed is a new dedicated tab in the bottom navigation bar. Streak entries are generated in real-time when the user checks off a habit, and persisted in a new database table. The feed covers both REGULAR and TRACKER habit types.

## Requirements (from interview)

| # | Question | Answer |
|---|----------|--------|
| 1 | What items appear in the feed? | Only habit streak milestones (e.g., "7-day streak on Exercise!") |
| 2 | Streak definition for habits with partial schedules? | Count only scheduled days consecutively (Mon→Wed→Fri = 3-day streak, ignoring off-days) |
| 3 | At what milestones? | Every day — show every streak increment (1, 2, 3, …) |
| 4 | Where in the app? | New dedicated tab in the bottom navigation bar |
| 5 | Visual design per item? | Minimal: habit name + streak count |
| 6 | Persistence? | Persisted in a new `ActivityFeedEntity` database table |
| 7 | Which habit types? | Both REGULAR and TRACKER habits |
| 8 | When are entries generated? | Immediately when the user checks off / logs a habit |
| 9 | Streak-broken notifications? | Yes — show both positive streak updates and streak-broken entries |

## Data Model

### New Entity: `ActivityFeedEntity`

```kotlin
@Entity(tableName = "activity_feed")
data class ActivityFeedEntity(
    @PrimaryKey val id: String,           // UUID
    val habitId: String,                   // FK to HabitEntity
    val habitTitle: String,                // Denormalized for fast display
    val type: ActivityFeedType,            // STREAK_INCREMENT or STREAK_BROKEN
    val streakCount: Int,                  // Current streak (or streak at time of break)
    val date: String,                      // ISO date when the event occurred
    val timestamp: Long                    // epoch millis for ordering
)
```

### Enum: `ActivityFeedType`

```kotlin
enum class ActivityFeedType {
    STREAK_INCREMENT,  // User continued their streak
    STREAK_BROKEN      // User missed a scheduled day, streak reset
}
```

### Database Migration

- Bump database version from 8 → 9.
- Add migration creating `activity_feed` table.

## Streak Calculation Logic

### Algorithm: `CalculateStreakUseCase`

Given a `habitId` and today's date:

1. Fetch the habit's `daysToCheck` (scheduled days of the week).
2. Fetch all `DailyEntity` records for this habit, ordered by date descending.
3. Walk backwards from today through only **scheduled days**:
   - If the scheduled day has a completion record → increment streak count.
   - If the scheduled day has no completion record → stop (streak is broken).
4. Return the streak count.

### For TRACKER habits

A tracker habit is considered "completed" for a given day if there exists a `TrackerEntity` record for that habit on that date (any value counts as logged).

### Streak-broken detection

When a user opens the app or checks a habit, compute the streak. If the previous scheduled day was missed (no completion), generate a `STREAK_BROKEN` entry (if one doesn't already exist for that break) before generating the new `STREAK_INCREMENT` entry.

## Files to Create

| File | Purpose |
|------|---------|
| `composeApp/src/commonMain/kotlin/feature/feed/data/ActivityFeedEntity.kt` | Room entity |
| `composeApp/src/commonMain/kotlin/feature/feed/data/ActivityFeedDao.kt` | Room DAO |
| `composeApp/src/commonMain/kotlin/feature/feed/domain/CalculateStreakUseCase.kt` | Streak calculation |
| `composeApp/src/commonMain/kotlin/feature/feed/domain/RecordStreakEventUseCase.kt` | Create feed entries on habit check |
| `composeApp/src/commonMain/kotlin/feature/feed/domain/GetActivityFeedUseCase.kt` | Query feed entries |
| `composeApp/src/commonMain/kotlin/feature/feed/domain/DetectBrokenStreaksUseCase.kt` | Detect and record broken streaks |
| `composeApp/src/commonMain/kotlin/feature/feed/domain/model/ActivityFeedType.kt` | Enum for feed item types |
| `composeApp/src/commonMain/kotlin/feature/feed/presentation/ActivityFeedViewModel.kt` | ViewModel |
| `composeApp/src/commonMain/kotlin/feature/feed/presentation/models/ActivityFeedViewState.kt` | State model |
| `composeApp/src/commonMain/kotlin/feature/feed/presentation/models/ActivityFeedEvent.kt` | Event model |
| `composeApp/src/commonMain/kotlin/feature/feed/presentation/models/ActivityFeedAction.kt` | Action model |
| `composeApp/src/commonMain/kotlin/feature/feed/ui/ActivityFeedScreen.kt` | Compose UI screen |
| `composeApp/src/commonMain/kotlin/feature/feed/ui/ActivityFeedItemView.kt` | Single feed item composable |

## Files to Modify

| File | Change |
|------|--------|
| `composeApp/src/commonMain/kotlin/core/database/AppDatabase.kt` | Add `ActivityFeedEntity` to `@Database` entities, add `activityFeedDao()` abstract method, bump version to 9, add migration 8→9 |
| `composeApp/src/commonMain/kotlin/core/di/dataModule.kt` (or equivalent DI config) | Register `ActivityFeedDao`, use cases, and ViewModel in Kodein |
| `composeApp/src/commonMain/kotlin/core/navigation/` (navigation graph) | Add Activity Feed tab/route to bottom navigation |
| `composeApp/src/commonMain/kotlin/feature/daily/domain/SwitchHabitUseCase.kt` | After toggling a habit, trigger `RecordStreakEventUseCase` |
| `composeApp/src/commonMain/kotlin/feature/tracker/domain/UpdateTrackerValueUseCase.kt` | After logging a tracker value, trigger `RecordStreakEventUseCase` |

## Implementation Approach

### Phase 1: Data Layer
1. Create `ActivityFeedEntity` and `ActivityFeedDao` with Room annotations.
2. Add database migration (v8 → v9) creating the `activity_feed` table.
3. Register the DAO in the DI module.

### Phase 2: Domain Layer
1. Implement `CalculateStreakUseCase` — walks backwards through scheduled days counting consecutive completions.
2. Implement `RecordStreakEventUseCase` — called when a habit is checked; calculates streak, creates `STREAK_INCREMENT` entry, and checks for broken streaks to create `STREAK_BROKEN` entries.
3. Implement `DetectBrokenStreaksUseCase` — scans habits for missed scheduled days since last check and generates `STREAK_BROKEN` entries.
4. Implement `GetActivityFeedUseCase` — returns feed entries ordered by timestamp descending.

### Phase 3: Presentation Layer
1. Create `ActivityFeedViewModel` extending `BaseViewModel` with state/event/action pattern matching existing ViewModels.
2. State includes: list of feed items, loading state, empty state.

### Phase 4: UI Layer
1. Create `ActivityFeedScreen` with a `LazyColumn` of feed items.
2. Each `ActivityFeedItemView` shows: habit title + streak count (minimal design). Streak-broken items should be visually distinct (e.g., different text/color).
3. Add the Activity Feed as a new tab in the bottom navigation bar.

### Phase 5: Integration
1. Hook `RecordStreakEventUseCase` into `SwitchHabitUseCase` (REGULAR habits).
2. Hook `RecordStreakEventUseCase` into `UpdateTrackerValueUseCase` (TRACKER habits).
3. Call `DetectBrokenStreaksUseCase` when the feed screen is opened (to catch missed days).

## Acceptance Criteria

- [ ] A new "Activity Feed" tab appears in the bottom navigation bar.
- [ ] When a user checks off a REGULAR habit, a streak entry is immediately created and visible in the feed.
- [ ] When a user logs a TRACKER habit value, a streak entry is immediately created and visible in the feed.
- [ ] Streak count correctly considers only scheduled days (per `daysToCheck`) when calculating consecutive completions.
- [ ] If a scheduled day was missed, a "streak broken" entry appears in the feed.
- [ ] Feed items show the habit name and streak count in a minimal format.
- [ ] Feed items are persisted in the database and survive app restarts.
- [ ] Feed is ordered by most recent first.
- [ ] The feature works on all supported platforms (Android, iOS, macOS, JVM Desktop).

## Edge Cases and Risks

| Edge Case | Handling |
|-----------|----------|
| Habit checked and unchecked same day | Unchecking should remove the feed entry for that day (or mark it invalid). Recalculate streak on uncheck. |
| Habit with no scheduled days | Skip streak calculation — no feed entries generated. |
| Habit created today (no history) | First check creates a streak of 1. |
| Multiple habits checked at once | Each generates its own independent feed entry. |
| App not opened for several days | When `DetectBrokenStreaksUseCase` runs, it should generate at most one `STREAK_BROKEN` per habit (for the most recent break), not one per missed day. |
| Database migration failure | Standard Room migration with fallback. Test migration path from v8. |
| Large feed history (performance) | Paginate the feed query (e.g., load 50 items at a time). Consider adding a database index on `timestamp`. |
| Habit deleted after feed entries exist | Feed entries remain as historical records (habitTitle is denormalized). Optionally clean up on habit deletion. |

## Out of Scope

- Social/multi-user activity feed (no backend/server component).
- Push notifications or system-level notifications.
- Configurable milestone thresholds (all streak increments are shown).
- Motivational messages, emojis, or rich visual design beyond minimal text.
- Filtering or searching the activity feed.
- Exporting or sharing streak data.
- Badge/achievement system.
