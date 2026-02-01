# Spec: Share Habit Streak

## Summary

Add the ability for users to share their current habit streak as a styled image card via the OS share sheet. The share button is accessible from the Statistics screen, generates a minimal branded card showing the habit name and consecutive-day streak count, and supports all platforms (Android, iOS, macOS, Desktop JVM).

## Requirements

### What is shared
- A **shareable image card** containing:
  - Habit title (e.g., "Morning Run")
  - Current streak count (e.g., "14 days")
  - App branding ("JetHabit")
- The card follows the user's current theme (light or dark mode) and active color style.

### Streak calculation
- **Current streak** = number of consecutive days the habit has been checked, counting backwards from **today**.
- The streak is **strict**: if today is not checked, the streak is 0. If today is checked but yesterday is not, the streak is 1.
- Only days the habit exists (between startDate and endDate) are considered. Days before the habit's startDate do not count.
- For habits with specific `daysToCheck`, all calendar days still count toward the streak (not just scheduled days). A missed non-scheduled day still breaks the streak.

### Entry point
- A **share button** on each habit's statistics item in the **Statistics screen** (`StatisticsScreen.kt`).
- The share button is **hidden/disabled when the streak is 0**.

### User flow
1. User opens the Statistics screen.
2. Each habit with a streak > 0 shows a share icon/button.
3. User taps the share button.
4. A **preview dialog** appears showing the rendered card.
5. User taps "Share" in the dialog to open the OS share sheet with the card image.
6. User can dismiss the dialog to cancel.

### Platform support
- **Android**: Render Compose card to `Bitmap`, share via `Intent.ACTION_SEND` with `image/png` MIME type using a `FileProvider`.
- **iOS**: Render to `UIImage` via `CGContext`/`UIGraphicsImageRenderer`, share via `UIActivityViewController`.
- **macOS**: Render to `NSImage`, share via `NSSharingServicePicker`.
- **Desktop JVM (non-macOS)**: Render to `BufferedImage`, save to temp file, open system share or file-save dialog.

### Visual design
- Card dimensions: ~1080x566px (roughly 1.91:1 ratio, good for social media).
- Background color: `JetHabitTheme.colors.primaryBackground` (theme-aware).
- Habit title: `JetHabitTheme.colors.primaryText`, prominent size.
- Streak count: Large, bold, accent color (`tintColor`).
- Branding: Small "JetHabit" text at bottom, secondary text color.
- Rounded corners on the card.

## Files to Create

| File | Purpose |
|------|---------|
| `feature/statistics/ui/ShareStreakCard.kt` | Composable for the share card layout |
| `feature/statistics/ui/ShareStreakDialog.kt` | Preview dialog with Share/Cancel buttons |
| `feature/statistics/presentation/models/StreakData.kt` | Data class for streak info passed to the card |
| `core/platform/ShareImage.kt` | `expect` declaration for platform image sharing |
| `androidMain/.../ShareImage.android.kt` | Android `actual` implementation (Intent + FileProvider) |
| `iosMain/.../ShareImage.ios.kt` | iOS `actual` implementation (UIActivityViewController) |
| `macosMain/.../ShareImage.macos.kt` | macOS `actual` implementation (NSSharingServicePicker) |
| `jvmMain/.../ShareImage.jvm.kt` | Desktop JVM `actual` implementation (file save) |

## Files to Modify

| File | Change |
|------|--------|
| `feature/statistics/presentation/StatisticsViewModel.kt` | Add streak calculation logic; expose streak per habit in ViewState |
| `feature/statistics/ui/models/StatisticsViewState.kt` | Add streak field to `HabitStatistics` or create wrapper |
| `feature/statistics/ui/StatisticsItem.kt` | Add share button (visible when streak > 0) |
| `feature/statistics/ui/StatisticsScreen.kt` | Wire up share dialog state and display |
| `domain/HabitStatistics.kt` | Add `currentStreak: Int` field |
| Platform `AndroidManifest.xml` or similar | Add FileProvider config for Android sharing if not present |

## Implementation Approach

### 1. Streak Calculation (ViewModel layer)

Add a function in `StatisticsViewModel` that, given a habit's list of `DailyEntity` records, calculates the current streak:

```kotlin
fun calculateCurrentStreak(habitId: String, dailyRecords: List<DailyEntity>): Int {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    var streak = 0
    var date = today
    while (true) {
        val dateStr = date.toString() // ISO format
        val record = dailyRecords.find { it.timestamp == dateStr && it.isChecked }
        if (record != null) {
            streak++
            date = date.minus(1, DateTimeUnit.DAY)
        } else {
            break
        }
    }
    return streak
}
```

Add `currentStreak: Int` to `HabitStatistics` data class and populate it during the statistics loading flow.

### 2. Share Card Composable

Create `ShareStreakCard` as a pure Composable that renders the card using `JetHabitTheme` colors. This composable is used both for the preview dialog and for bitmap capture.

### 3. Preview Dialog

`ShareStreakDialog` wraps the card in an `AlertDialog` or custom dialog with "Share" and "Cancel" actions.

### 4. Platform Image Sharing (`expect`/`actual`)

Define a common interface:

```kotlin
// commonMain
expect class ImageSharer {
    fun shareImage(bitmap: ImageBitmap, title: String)
}
```

Each platform implements this differently:
- **Android**: Write `ImageBitmap` to a temp file via `FileProvider`, launch `Intent.ACTION_SEND`.
- **iOS**: Convert to `UIImage`, present `UIActivityViewController`.
- **macOS**: Convert to `NSImage`, present `NSSharingServicePicker`.
- **Desktop JVM**: Convert to `BufferedImage`, use `JFileChooser` to save or copy to clipboard.

### 5. Bitmap Capture

Use Compose's `GraphicsLayer` or `Canvas`-based approach to render the `ShareStreakCard` composable to an `ImageBitmap` off-screen. This is the trickiest cross-platform piece:
- On Android: `AndroidView` with `View.drawToBitmap()` or `Picture`-based recording.
- On other platforms: Use Compose's `drawIntoCanvas` / `Canvas` APIs.

Alternatively, render the composable into a `GraphicsLayer`, call `toImageBitmap()` (available in newer Compose Multiplatform versions).

### 6. Statistics Screen Integration

Add a share icon button to `StatisticsItem`. On click, set dialog state in the screen to show `ShareStreakDialog` for that habit.

## Acceptance Criteria

1. Statistics screen shows a share button on each habit with a streak > 0.
2. Habits with streak = 0 do not show the share button.
3. Tapping share opens a preview dialog with the correctly themed card.
4. The card displays the correct habit name, streak count, and "JetHabit" branding.
5. Tapping "Share" in the dialog opens the native OS share sheet with the card as a PNG image.
6. Dismissing the dialog cancels the share flow.
7. Works on Android, iOS, macOS, and Desktop JVM.
8. Card respects current light/dark theme and color style.

## Edge Cases and Risks

| Edge Case | Handling |
|-----------|----------|
| Streak is 0 | Share button hidden/disabled |
| Habit has no daily records at all | Streak is 0, button hidden |
| Habit endDate is in the past | Still calculate streak (may be 0 if not checked recently) |
| Very long habit title | Truncate with ellipsis on the card |
| Very large streak number (e.g., 1000+) | Display as-is; card layout should handle large numbers |
| Bitmap capture fails | Show error toast/snackbar, don't crash |
| Share sheet dismissed by user | No action needed, dialog stays open for retry |
| Platform share API unavailable | Fallback to clipboard copy or file save |

### Risks
- **Compose-to-bitmap capture** is the highest-risk area. Compose Multiplatform's `GraphicsLayer.toImageBitmap()` API availability varies across versions. May need platform-specific capture approaches.
- **iOS/macOS interop**: Presenting `UIActivityViewController` / `NSSharingServicePicker` from Compose requires bridging to native view controllers.
- **FileProvider setup on Android**: Needs XML configuration and manifest entry if not already present.

## Out of Scope

- Sharing completion rate, tracker progress, or calendar heatmaps (future enhancement).
- Text-based sharing or clipboard copy (only image card for now).
- Deep links or social media-specific integrations.
- Custom card themes or user-selectable card designs.
- Sharing from screens other than Statistics.
- Milestone/badge system.
- Scheduled days-only streak counting (all calendar days count).
