# Spec: Share Habits Streak

## Summary

Add the ability for users to share their habit streak as a visually appealing image card via the platform's native share sheet. Users can share from the habit detail screen, generating a card that includes the habit name, current streak count, completion rate, and a mini calendar/heatmap visualization.

## Requirements

### Functional Requirements

1. **Share Button on Detail Screen**: Add a share button (icon) to the habit detail screen that triggers the share flow.
2. **Streak Calculation**: Implement streak calculation logic — count consecutive days a habit was checked, ending at the current date (or the most recent checked date).
3. **Shareable Image Generation**: Render a Compose-based "streak card" to a bitmap image containing:
   - Habit title
   - Current streak count (e.g., "15 days")
   - Completion rate percentage
   - Good/bad habit indicator (visual styling difference)
   - App branding/watermark ("JetHabit")
4. **Platform Share Sheet**: Use each platform's native share mechanism to share the generated image.
5. **Text Fallback**: Also attach a text summary (e.g., "I've kept up my 'Morning Run' habit for 15 days straight! #JetHabit") as fallback content for platforms that prefer text.

### Non-Functional Requirements

- Sharing must work on Android and iOS at minimum. Desktop and web can show a "copy to clipboard" fallback.
- Image generation should be fast (< 1 second).
- The share card should look good on both light and dark backgrounds (use a self-contained card design with its own background).

## Interview Notes

The user was not available for detailed interview questions. The following assumptions were made based on the task description ("share my habits streak to other people") and common patterns for habit-sharing features:

- **Format**: Shareable image card via OS share sheet (most common and visually appealing approach for social sharing)
- **Scope**: Share from the detail screen for a single habit (not bulk sharing)
- **Streak definition**: Consecutive days of completion ending at today or the last checked date
- **Platforms**: Android and iOS as primary targets, with clipboard fallback for desktop/web

## Files to Create

| File | Purpose |
|------|---------|
| `composeApp/src/commonMain/kotlin/feature/detail/domain/CalculateStreakUseCase.kt` | Calculate current streak for a habit |
| `composeApp/src/commonMain/kotlin/feature/share/ui/StreakCard.kt` | Composable that renders the shareable streak card |
| `composeApp/src/commonMain/kotlin/feature/share/presentation/ShareViewModel.kt` | ViewModel managing share state and image generation |
| `composeApp/src/commonMain/kotlin/core/platform/ShareService.kt` | Expect declaration for platform sharing |
| `composeApp/src/androidMain/kotlin/core/platform/AndroidShareService.kt` | Android actual: Intent.ACTION_SEND with image |
| `composeApp/src/iosMain/kotlin/core/platform/IOSShareService.kt` | iOS actual: UIActivityViewController |
| `composeApp/src/desktopMain/kotlin/core/platform/DesktopShareService.kt` | Desktop actual: Copy image to clipboard |
| `composeApp/src/jsMain/kotlin/core/platform/JsShareService.kt` | Web actual: Web Share API or clipboard fallback |

## Files to Modify

| File | Change |
|------|--------|
| `composeApp/src/commonMain/kotlin/feature/detail/ui/DetailView.kt` | Add share button to the UI |
| `composeApp/src/commonMain/kotlin/feature/detail/presentation/DetailViewModel.kt` | Add share action/event handling |
| `composeApp/src/commonMain/kotlin/feature/detail/models/DetailViewState.kt` | Add streak count and share-related state |
| `composeApp/src/commonMain/kotlin/feature/detail/models/DetailEvent.kt` | Add share event |
| `composeApp/src/commonMain/kotlin/di/PlatformSDK.kt` | Register ShareService in DI |
| `composeApp/src/androidMain/kotlin/di/Providers.kt` | Provide Android ShareService |
| `composeApp/src/iosMain/kotlin/di/Providers.kt` | Provide iOS ShareService |
| `composeApp/src/desktopMain/kotlin/di/Providers.kt` | Provide Desktop ShareService |

## Detailed Implementation Approach

### 1. Streak Calculation (`CalculateStreakUseCase`)

```kotlin
class CalculateStreakUseCase(private val dailyDao: DailyDao) {
    suspend fun execute(habitId: String): Int {
        // Get all DailyEntity records for this habit, sorted by date descending
        // Walk backwards from today counting consecutive checked days
        // Return the streak count (0 if no streak)
    }
}
```

- Query all `DailyEntity` rows for the habit, filter `isChecked == true`
- Sort by timestamp descending
- Starting from today (or most recent checked day), count consecutive days
- Account for `daysToCheck` — if a habit is only tracked Mon/Wed/Fri, skip non-tracked days when calculating streak

### 2. Streak Card Composable (`StreakCard`)

A self-contained Composable that renders:
```
┌─────────────────────────┐
│  🔥 Morning Run         │
│                         │
│      15 days            │
│    current streak       │
│                         │
│  Completion: 87%        │
│                         │
│       JetHabit          │
└─────────────────────────┘
```

- Fixed dimensions suitable for sharing (e.g., 400x500dp)
- Card background with rounded corners
- Color-coded: green tones for good habits, red/orange for bad habits
- Uses the app's existing theme colors where possible
- Self-contained (doesn't rely on external theme — includes its own background)

### 3. Platform Share Service (expect/actual pattern)

```kotlin
// Common
expect class ShareService {
    fun shareImage(imageBytes: ByteArray, text: String)
}
```

**Android**: Use `Intent.ACTION_SEND` with `image/png` MIME type. Write bitmap to a `FileProvider` URI. Include text as `Intent.EXTRA_TEXT`.

**iOS**: Use `UIActivityViewController` with the image and text.

**Desktop**: Copy image to system clipboard using `java.awt.Toolkit`.

**Web**: Use `navigator.share()` Web Share API if available, otherwise fall back to copying text to clipboard.

### 4. Image Generation

Use Compose's `GraphicsLayer` or `drawToBitmap` approach:
- On Android: Use `AndroidView` or Compose's `ImageBitmap` rendering
- Cross-platform: Render the `StreakCard` composable to a bitmap using `captureToImage()` from compose test utils, or use a `Canvas`-based approach that works cross-platform

Alternative simpler approach: Use Compose `Canvas` to draw the card directly to a `ImageBitmap`, avoiding the need to render a composable off-screen. This is more portable across platforms.

### 5. Integration into Detail Screen

- Add a share `IconButton` (share icon) in the top-right area of `DetailView`
- On click, dispatch `DetailEvent.ShareClicked`
- ViewModel calculates streak, triggers image generation, then calls `ShareService`
- Show a brief loading indicator if image generation takes time

## Acceptance Criteria

1. User can tap a share button on the habit detail screen
2. A visually appealing streak card image is generated showing habit name, streak count, and completion rate
3. The platform's native share sheet opens with the image and text
4. Streak count correctly reflects consecutive days of habit completion
5. The feature works on Android and iOS
6. Desktop/web platforms have a reasonable fallback (clipboard)
7. The share card is readable and looks good when shared to common platforms (WhatsApp, Instagram Stories, Twitter)

## Edge Cases and Risks

### Edge Cases
- **Zero streak**: Show "0 days" with encouraging messaging (e.g., "Just getting started!")
- **Habit with no check-ins**: Still allow sharing with 0 streak
- **Tracker-type habits**: Show streak based on days where a value was recorded
- **Habits with specific days**: Streak should only consider the days the habit is tracked (e.g., MWF habit — missing Tuesday doesn't break the streak)
- **Deleted habit data**: Handle gracefully if habit has no daily records
- **Very long streaks**: Ensure the number displays correctly (e.g., "1,234 days")

### Risks
- **Cross-platform image generation**: Rendering a Composable to bitmap differs across platforms. May need platform-specific image generation code. **Mitigation**: Use Canvas-based drawing that works uniformly.
- **File provider setup on Android**: Sharing images requires a `FileProvider` entry in AndroidManifest.xml. **Mitigation**: Add the provider configuration.
- **iOS image sharing**: UIActivityViewController requires platform-specific Kotlin/Native interop. **Mitigation**: Follow the existing `IOSImagePicker` pattern.
- **Image quality**: Bitmap rendering might look blurry on high-DPI screens. **Mitigation**: Render at 2x or 3x scale.

## Out of Scope

- Live/real-time habit sharing between users (social features)
- Sharing entire habit history or detailed statistics
- Sharing multiple habits at once
- Deep links that open the app
- User accounts or cloud sync
- Habit import/export functionality
- Sharing from screens other than the detail screen (can be added later)
- Custom card themes or templates
