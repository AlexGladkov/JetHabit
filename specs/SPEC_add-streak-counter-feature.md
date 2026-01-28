# Specification: Add Streak Counter Feature

## Overview
Add streak tracking functionality to the JetHabit app to motivate users by showing their consecutive completion streaks for each habit.

## Requirements

### 1. Data Layer Changes

#### 1.1 Database Schema
- Add the following columns to `HabitEntity`:
  - `currentStreak: Int` - The current active streak count (default: 0)
  - `longestStreak: Int` - The longest streak ever achieved (default: 0)
  - `lastCompletedDate: String?` - The last date the habit was completed (nullable, format: "YYYY-MM-DD")

- Create database migration `Migration8to9` to add these columns to the existing database

#### 1.2 DAO Methods
- Add methods to `HabitDao`:
  - `updateStreakInfo(habitId, currentStreak, longestStreak, lastCompletedDate)` - Update streak information for a habit
  - `getStreakInfo(habitId)` - Retrieve streak information for a habit

- Add methods to `DailyDao`:
  - `getCompletedDatesForHabit(habitId)` - Get all dates where the habit was checked (descending order)

### 2. Domain Layer Changes

#### 2.1 New Use Cases

**CalculateStreakUseCase**
- **Purpose**: Calculate the current streak for a habit based on completion history
- **Input**: `habitId: String`, `targetDate: LocalDate` (default: today)
- **Logic**:
  1. Fetch all completed dates for the habit from DailyEntity
  2. Starting from targetDate, count backward consecutive days where the habit was completed
  3. Account for the habit's `daysToCheck` - only count days the habit is scheduled for
  4. Stop counting when a scheduled day is missed
  5. Return current streak count
- **Output**: `Int` (streak count)

**UpdateStreakUseCase**
- **Purpose**: Update streak information in the database after calculating
- **Input**: `habitId: String`
- **Logic**:
  1. Use CalculateStreakUseCase to get current streak
  2. Fetch current longestStreak from database
  3. Update longestStreak if currentStreak is greater
  4. Update lastCompletedDate from the most recent DailyEntity entry
  5. Save updated values to HabitEntity
- **Output**: Unit

#### 2.2 Modified Use Cases

**SwitchHabitUseCase**
- After checking/unchecking a habit, call `UpdateStreakUseCase` to recalculate the streak

### 3. Presentation Layer Changes

#### 3.1 Data Models

**Add to DailyHabit** (`/feature/daily/domain/models/DailyHabit.kt`):
```kotlin
val currentStreak: Int = 0
val longestStreak: Int = 0
```

**Add to HabitCardItemModel** (`/feature/daily/ui/views/HabitCardItem.kt`):
```kotlin
val currentStreak: Int = 0
```

**Add to HabitDetailModel** (`/feature/detail/ui/models/HabitDetailModel.kt`):
```kotlin
val currentStreak: Int = 0
val longestStreak: Int = 0
val lastCompletedDate: String? = null
```

#### 3.2 ViewModels

**GetHabitsForTodayUseCase**
- Include streak information when building DailyHabit objects

**DetailViewModel**
- Include streak information when building HabitDetailModel

### 4. UI Layer Changes

#### 4.1 Daily View - HabitCardItem
- Display a streak badge when `currentStreak > 0`
- Badge format: "🔥 {count}"
- Position: Next to the habit title
- Style: Small text, accent color (orange/red)
- Example: "Morning Run 🔥 7"

#### 4.2 Detail View
- Add a "Streak Statistics" section showing:
  - Current Streak: "{count} days"
  - Longest Streak: "{count} days"
  - Last Completed: "{date}" (formatted as user-friendly date)
- Position: Below habit metadata, above action buttons
- Style: Consistent with existing detail view sections

### 5. Dependency Injection

- Register `CalculateStreakUseCase` in `HabitModule` or `DailyModule`
- Register `UpdateStreakUseCase` in `HabitModule` or `DailyModule`
- Inject into `SwitchHabitUseCase`, `GetHabitsForTodayUseCase`, and `DetailViewModel`

### 6. Business Rules

#### Streak Calculation Logic
1. A streak is only counted on days where the habit is scheduled (based on `daysToCheck`)
2. If a habit is scheduled for Monday/Wednesday/Friday:
   - Completing Mon, Wed, Fri = 3 day streak
   - Missing Wednesday breaks the streak
   - Tuesday and Thursday don't affect the streak (not scheduled)
3. The streak starts at 0 and increases by 1 for each consecutive scheduled day completed
4. Missing a scheduled day resets the current streak to 0
5. The longest streak is never decreased, only updated if current streak exceeds it
6. For new habits with no completions, current streak = 0

#### Edge Cases
- If today is not a scheduled day, count up to yesterday
- If the habit's end date is in the past, calculate streak up to the end date
- If a habit is checked and unchecked on the same day, recalculate the streak
- Tracker-type habits are not included in streak tracking (REGULAR habits only)

### 7. Testing Considerations

Manual testing scenarios:
1. Create a new habit scheduled for all days
2. Check the habit for 3 consecutive days - verify streak shows "🔥 3"
3. Skip a day, then check again - verify streak resets to "🔥 1"
4. Check habit for 10 days to exceed previous longest - verify longest streak updates
5. Create a habit scheduled for Mon/Wed/Fri only - verify streak only counts those days
6. Uncheck a habit that had a streak - verify streak recalculates correctly
7. View detail page - verify current streak, longest streak, and last completed date display correctly

### 8. Migration Strategy

- Database version increment: 8 → 9
- Migration must handle existing habits by:
  - Setting `currentStreak = 0`
  - Setting `longestStreak = 0`
  - Setting `lastCompletedDate = null`
- After migration, existing users will start with 0 streaks
- Optional: Add a post-migration calculation to populate historical streaks (future enhancement)

## Implementation Order

1. Database schema changes and migration
2. DAO methods
3. Domain use cases
4. Update existing use cases
5. Presentation models
6. UI components
7. Dependency injection
8. Testing

## Success Criteria

- Users can see their current streak for each habit in the daily view
- Streaks are calculated correctly based on consecutive completions on scheduled days
- The longest streak is tracked and displayed in the detail view
- Checking/unchecking habits immediately updates the streak count
- The feature works correctly across all supported platforms (Android, iOS, Web, Desktop)
