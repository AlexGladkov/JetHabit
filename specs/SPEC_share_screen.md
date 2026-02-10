# Spec: Share Screen — Habit Achievement Sharing

## Summary

Add the ability for users to share their habit achievements as a visual image card. A "Share All" button on the Statistics screen generates a branded card showing the user's top 5 habits (sorted by current streak length), each with title, streak count, and completion rate. The card is displayed in a preview screen before being shared via the platform's native share sheet. The feature targets Android and iOS only; Desktop platforms will not show the share button.

## Requirements (from Interview)

| Question | Answer |
|----------|--------|
| Share format | Visual image card (not text) |
| Card data | Habit title, completion rate, current streak count (no calendar grid) |
| Card layout | Compact list — each habit as a row with title + streak + completion % |
| Habit limit | Top 5 habits by longest current streak; "+N more habits" note if more exist |
| Trigger location | Statistics screen only — a single "Share All" button |
| Card design | Fixed branded "JetHabit" design (not theme-dependent) |
| Branding | "JetHabit" text at the bottom of the card (no logo) |
| Preview | Show a preview screen/dialog before opening the native share sheet |
| Platforms | Android and iOS only; Desktop share button hidden/disabled |
| Desktop handling | Skip Desktop — do not implement sharing on JVM |

## Architecture Overview

```
Statistics Screen
    ↓ (tap "Share All")
ShareViewModel (calculates streaks, selects top 5)
    ↓
Share Preview Screen/Dialog (renders card as Composable)
    ↓ (tap "Share" to confirm)
Platform ShareService (expect/actual)
    ├── Android: Render composable to Bitmap → save to cache → Intent.ACTION_SEND
    └── iOS: Render composable to UIImage → UIActivityViewController
```

## Files to Create

| File | Purpose |
|------|---------|
| `feature/share/presentation/ShareViewModel.kt` | ViewModel: calculates streaks, selects top 5 habits, manages preview state |
| `feature/share/presentation/models/ShareViewState.kt` | State model for the share screen |
| `feature/share/presentation/models/ShareEvent.kt` | Events: TapShare, ConfirmShare, Dismiss |
| `feature/share/presentation/models/ShareAction.kt` | Actions: OpenShareSheet, ShowError |
| `feature/share/domain/CalculateStreaksUseCase.kt` | Use case: calculates current streak per habit from DailyEntity records |
| `feature/share/domain/GetTopHabitsForSharingUseCase.kt` | Use case: gets top 5 habits by streak, returns `ShareableHabit` list |
| `feature/share/domain/models/ShareableHabit.kt` | Domain model: habit title, current streak, completion rate |
| `feature/share/ui/SharePreviewScreen.kt` | Preview screen/dialog showing the generated card |
| `feature/share/ui/ShareCardContent.kt` | The composable that renders the branded share card |
| `feature/share/platform/ShareService.kt` | `expect` class for platform share functionality |
| `composeApp/src/androidMain/kotlin/.../share/platform/ShareServiceImpl.kt` | Android `actual` — bitmap rendering + Intent share |
| `composeApp/src/iosMain/kotlin/.../share/platform/ShareServiceImpl.kt` | iOS `actual` — UIImage rendering + UIActivityViewController |
| `composeApp/src/jvmMain/kotlin/.../share/platform/ShareServiceImpl.kt` | JVM `actual` — no-op stub |
| `di/ShareModule.kt` | Kodein DI bindings for share feature |

## Files to Modify

| File | Change |
|------|--------|
| `feature/statistics/ui/StatisticsScreen.kt` | Add "Share All" button (hidden on Desktop via `LocalPlatform`) |
| `navigation/` (relevant nav files) | Add navigation route for SharePreviewScreen |
| `core/database/AppDatabase.kt` | No change needed — uses existing DAOs |
| `di/FeatureModule.kt` or `di/CoreModule.kt` | Import and bind ShareModule |

## Detailed Implementation Approach

### 1. Streak Calculation (`CalculateStreaksUseCase`)

Currently, `StatisticsViewModel` calculates completion rate and `TrackedDay` lists but does not compute a "current streak" (consecutive days). A new use case is needed:

```
CalculateStreaksUseCase.execute(habitId: String): Int
  1. Get habit entity (for startDate, daysToCheck)
  2. Starting from today, walk backwards day by day
  3. For each day that is in daysToCheck:
     - Query DailyDao.isHabitChecked(habitId, date)
     - If checked → increment streak counter
     - If not checked → break
  4. Return streak count
```

### 2. Top Habits Selection (`GetTopHabitsForSharingUseCase`)

```
GetTopHabitsForSharingUseCase.execute(): ShareCardData
  1. Get all habits via HabitDao.getAll()
  2. Filter to REGULAR type only (tracker habits don't have streaks)
  3. For each habit:
     - Calculate current streak via CalculateStreaksUseCase
     - Calculate completion rate (reuse logic from StatisticsViewModel)
  4. Sort by streak descending
  5. Take top 5
  6. Return ShareCardData(habits: List<ShareableHabit>, totalHabitCount: Int)
```

`ShareableHabit`:
```kotlin
data class ShareableHabit(
    val title: String,
    val currentStreak: Int,        // consecutive days
    val completionRate: Float      // 0.0 to 1.0
)

data class ShareCardData(
    val habits: List<ShareableHabit>,  // max 5
    val totalHabitCount: Int           // for "+N more" display
)
```

### 3. Share Card Composable (`ShareCardContent`)

A composable that renders the branded card. This composable will be rendered to a bitmap for sharing.

**Card Layout (fixed branded design):**
```
┌──────────────────────────────┐
│     My Habit Achievements    │  ← Header
│                              │
│  Morning Run                 │
│  🔥 45 days  •  92%         │  ← Streak + completion
│                              │
│  Read Books                  │
│  🔥 30 days  •  85%         │
│                              │
│  Meditate                    │
│  🔥 21 days  •  78%         │
│                              │
│  Exercise                    │
│  🔥 14 days  •  70%         │
│                              │
│  Drink Water                 │
│  🔥 7 days   •  65%         │
│                              │
│  +3 more habits              │  ← Only if totalCount > 5
│                              │
│              JetHabit        │  ← Branding footer
└──────────────────────────────┘
```

**Design specs:**
- Background: solid dark or gradient (fixed, not theme-dependent)
- Text: white or light colored
- Streak icon: flame emoji or custom drawn icon
- Card dimensions: fixed width (e.g., 400dp), height wraps content
- Corner radius for rounded card look
- "JetHabit" text centered at bottom in smaller/lighter font

### 4. Platform Share Service (`expect/actual`)

```kotlin
// commonMain
expect class ShareService {
    fun shareImage(imageBytes: ByteArray, title: String)
}

// androidMain
actual class ShareService(private val context: Context) {
    actual fun shareImage(imageBytes: ByteArray, title: String) {
        // 1. Save imageBytes to cache dir as PNG
        // 2. Get URI via FileProvider
        // 3. Create Intent.ACTION_SEND with image/png MIME type
        // 4. Start chooser activity
    }
}

// iosMain
actual class ShareService {
    actual fun shareImage(imageBytes: ByteArray, title: String) {
        // 1. Convert ByteArray to NSData → UIImage
        // 2. Create UIActivityViewController with image
        // 3. Present from root view controller
    }
}

// jvmMain
actual class ShareService {
    actual fun shareImage(imageBytes: ByteArray, title: String) {
        // No-op or throw UnsupportedOperationException
    }
}
```

### 5. Composable-to-Bitmap Rendering

Use Compose's `GraphicsLayer` or `ImageBitmap` capture API to render the card composable to a bitmap:

- **Android:** Use `AndroidView` with Canvas or `Picture` recording, or the `drawToBitmap()` extension
- **iOS:** Use Skia/Skiko rendering from Compose Multiplatform, or render via `UIGraphicsImageRenderer`

This is the most platform-specific part. The approach will use `expect/actual` for the bitmap capture step.

### 6. Navigation

Add a new route for the share preview:
- Route: `"share_preview"` within the Statistics navigation graph
- Or use a dialog/bottom sheet overlay (simpler, avoids full navigation)

Recommended: **Modal dialog** overlaying the Statistics screen, since it's a transient action.

### 7. Statistics Screen Modification

In `StatisticsScreen.kt`, add a "Share" floating action button or top-bar action:
```kotlin
// Only show on mobile platforms
if (currentPlatform != Platform.JVM) {
    FloatingActionButton(onClick = { showSharePreview = true }) {
        Icon(shareIcon, "Share achievements")
    }
}
```

### 8. DI Setup (`ShareModule`)

Register in Kodein:
```kotlin
val shareModule = DI.Module("shareModule") {
    bind<CalculateStreaksUseCase>() with provider { CalculateStreaksUseCase(instance()) }
    bind<GetTopHabitsForSharingUseCase>() with provider { GetTopHabitsForSharingUseCase(instance(), instance()) }
    bind<ShareService>() with singleton { ShareService(/* platform deps */) }
}
```

## Acceptance Criteria

1. **Statistics screen** shows a "Share" button (FAB or toolbar icon) on Android and iOS
2. **Share button is hidden** on Desktop/JVM platforms
3. Tapping "Share" opens a **preview dialog/screen** showing the branded card
4. The card displays **top 5 habits** sorted by current streak (descending)
5. Each habit row shows: **title**, **current streak** (days), **completion rate** (%)
6. If user has more than 5 habits, card shows **"+N more habits"** at the bottom
7. Card has **"JetHabit" text** at the bottom as branding
8. Card uses a **fixed branded design** (not theme-dependent)
9. From the preview, tapping "Share" opens the **native share sheet** with the card as an image
10. On **Android**: share via `Intent.ACTION_SEND` with image URI
11. On **iOS**: share via `UIActivityViewController` with UIImage
12. If the user has **no habits**, the share button should be disabled or show an appropriate message
13. Streak calculation correctly counts **consecutive completed days** backwards from today, only counting days that are in the habit's `daysToCheck`

## Edge Cases and Risks

### Edge Cases
- **No habits exist** — Disable the share button or show a message ("No habits to share")
- **All habits have 0-day streaks** — Still show the card with 0-day streaks (user may want to share they started tracking)
- **Only 1-4 habits exist** — Show all habits, no "+N more" note
- **Habit with no daysToCheck** — Skip in streak calculation (shouldn't happen but guard against it)
- **Very long habit titles** — Truncate with ellipsis on the card to prevent overflow

### Risks
- **Composable-to-bitmap rendering** — This is the highest-risk area. Compose Multiplatform's support for rendering composables to bitmaps varies by platform. May need platform-specific rendering approaches.
- **Image quality** — Need to render at sufficient resolution (2x or 3x) for the image to look good when shared on social media
- **iOS UIActivityViewController** — Requires access to the root UIViewController, which needs careful handling in Compose Multiplatform on iOS
- **File provider setup on Android** — Need to configure `FileProvider` in AndroidManifest.xml for sharing cached image files
- **Memory** — Rendering a bitmap in memory for large cards; should be manageable given the compact card design
- **Compose Multiplatform bitmap APIs** — May need to use `Canvas` + `drawIntoCanvas` with Skia for cross-platform bitmap generation rather than platform-specific APIs

### Mitigations
- Prototype the bitmap rendering early to validate the approach
- Use Skia (already included via Compose Multiplatform) for cross-platform bitmap generation where possible
- Keep the card design simple to reduce rendering complexity

## Out of Scope

- **Desktop/JVM sharing** — Explicitly excluded per requirements
- **Text-based sharing** — Only image card sharing
- **Per-habit sharing** — Only "Share All" is in scope (no individual habit share buttons)
- **Share from Detail screen** — Only Statistics screen
- **Theme-dependent card styling** — Card uses fixed branded design
- **Calendar grid on card** — Excluded to keep the card compact
- **Tracker-type habits on card** — Only REGULAR habits with boolean streaks (tracker habits don't have streak concept)
- **Share history/analytics** — No tracking of how often or where users share
- **Deep links** — No link back to the app from shared content
- **Web/JS platform** — Already disabled in the project
