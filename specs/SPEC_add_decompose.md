# Spec: Replace ViewModel with Decompose Components

## Summary

Migrate the entire JetHabit application from Jetpack Compose Navigation + `BaseViewModel` (androidx.lifecycle.ViewModel) architecture to Arkadii Ivanov's **Decompose** library. This includes:

1. Replacing all `BaseViewModel` subclasses with Decompose `ComponentContext`-based components
2. Replacing Jetpack Compose Navigation (`NavHost`/`NavController`) with Decompose's `Child Stack` navigation
3. Adopting Decompose's `Value<State>` for state management instead of `StateFlow`
4. Using Essenty lifecycle-coroutines for coroutine scope management
5. Adopting proper Kodein `DIAware` pattern for dependency injection (replacing `Inject.instance()` service locator)

## Requirements Gathered

| Question | Answer |
|----------|--------|
| Scope of migration | Replace ALL ViewModels across the entire project |
| Navigation replacement | Full Decompose adoption — both navigation AND component logic |
| Bottom nav structure | Nested Child Stacks — root stack + per-tab stacks preserving tab state |
| Dependency injection | Keep Kodein DI (already in project), migrate to proper DIAware pattern |
| Coroutine management | Essenty lifecycle-coroutines (scope auto-cancelled on component destroy) |
| State management | Decompose's `Value<State>` instead of StateFlow |
| Splash screen | Keep as a Decompose component in root Child Stack |
| iOS integration | Compose Multiplatform on all platforms (no SwiftUI/UIKit needed) |

## Current Architecture

### Navigation Structure
- **Root level** (`App.kt`): `NavHost` with routes: Splash → Main → Create
- **Main level** (`MainScreen.kt`): Nested `NavHost` with bottom navigation tabs:
  - **Daily** tab: Start → Detail/{habitId}
  - **Health** tab: Start → Track/{habitId} → Create?type={type}
  - **Statistics** tab: single screen
  - **Chat** tab: single screen
  - **Profile** tab: Start → Settings → Edit → Projects

### ViewModel Pattern
- `BaseViewModel<State, Action, Event>` extends `androidx.lifecycle.ViewModel`
- State exposed via `StateFlow<State>`
- One-shot actions via `SharedFlow<Action?>`
- Events received via `obtainEvent(Event)`
- Dependencies obtained via `Inject.instance<T>()` (service locator)
- Coroutines via `viewModelScope`

### Existing ViewModels (12 total)
1. `DailyViewModel` — daily habits list
2. `DetailViewModel` — habit detail view
3. `HealthViewModel` — health habits list
4. `TrackHabitViewModel` — track a health habit
5. `ComposeViewModel` (feature/create) — create/compose habit
6. `ComposeViewModel` (screens/compose) — legacy compose screen
7. `StatisticsViewModel` (feature/statistics) — statistics view
8. `StatisticsViewModel` (screens/stats) — legacy statistics
9. `ChatViewModel` — chat feature
10. `ProfileViewModel` — profile start screen
11. `EditProfileViewModel` — edit profile
12. `SettingsViewModel` — settings screen
13. `ProjectListViewModel` — project management
14. `MedicationAddNameViewModel` — medication naming

### DI Structure
- `PlatformSDK` — global Kodein DI container (singleton, initialized at app startup)
- `Inject.instance<T>()` — service locator shorthand
- Feature modules: `DailyModule`, `DetailModule`, `HabitModule`, `ProjectModule`, `SettingsModule`, `TrackerModule`
- Platform modules: `CoreModule`, `DatabaseModule`, `FeatureModule`, `SerializationModule`

## Target Architecture

### Decompose Component Hierarchy

```
RootComponent (Child Stack)
├── SplashComponent
├── MainComponent (manages bottom nav)
│   └── Per-tab Child Stacks:
│       ├── DailyComponent (Child Stack)
│       │   ├── DailyListComponent
│       │   └── DetailComponent(habitId)
│       ├── HealthComponent (Child Stack)
│       │   ├── HealthListComponent
│       │   ├── TrackHabitComponent(habitId)
│       │   └── CreateHabitComponent(type?)
│       ├── StatisticsComponent (leaf)
│       ├── ChatComponent (leaf)
│       └── ProfileComponent (Child Stack)
│           ├── ProfileStartComponent
│           ├── SettingsComponent
│           ├── EditProfileComponent
│           └── ProjectListComponent
└── CreateHabitFlowComponent (from root-level create route)
```

### Component Base Pattern

Each component will:
- Implement `ComponentContext` by delegation
- Implement `DIAware` to receive Kodein DI from parent
- Use `Value<State>` for observable state
- Use `coroutineScope(Dispatchers.Main)` from Essenty for async work
- Expose an `onEvent(Event)` method for UI interaction (or direct methods)
- Use sealed class `Config` with `@Serializable` for navigation configurations

### Example Component (DailyListComponent)

```kotlin
class DailyListComponent(
    componentContext: ComponentContext,
    override val di: DI,
    private val onHabitSelected: (String) -> Unit,
    private val onComposeClicked: () -> Unit
) : ComponentContext by componentContext, DIAware {

    private val getHabitsForTodayUseCase: GetHabitsForTodayUseCase by di.instance()
    private val switchHabitUseCase: SwitchHabitUseCase by di.instance()

    private val scope = coroutineScope(Dispatchers.Main)

    private val _state = MutableValue(DailyViewState())
    val state: Value<DailyViewState> = _state

    fun onEvent(event: DailyEvent) { ... }
}
```

### Navigation Config Pattern

```kotlin
@Serializable
sealed class RootConfig {
    @Serializable data object Splash : RootConfig()
    @Serializable data object Main : RootConfig()
    @Serializable data object Create : RootConfig()
}
```

## Files to Create

| File | Purpose |
|------|---------|
| `composeApp/src/commonMain/kotlin/root/RootComponent.kt` | Root component with Child Stack (Splash/Main/Create) |
| `composeApp/src/commonMain/kotlin/root/RootContent.kt` | Root Composable rendering Child Stack |
| `composeApp/src/commonMain/kotlin/feature/main/MainComponent.kt` | Main screen component managing bottom nav tabs |
| `composeApp/src/commonMain/kotlin/feature/main/MainContent.kt` | Main screen Composable with bottom nav + per-tab content |
| `composeApp/src/commonMain/kotlin/feature/daily/presentation/DailyComponent.kt` | Daily tab stack component |
| `composeApp/src/commonMain/kotlin/feature/daily/presentation/DailyListComponent.kt` | Daily list component (replaces DailyViewModel) |
| `composeApp/src/commonMain/kotlin/feature/detail/presentation/DetailComponent.kt` | Detail component (replaces DetailViewModel) |
| `composeApp/src/commonMain/kotlin/feature/health/list/presentation/HealthComponent.kt` | Health tab stack component |
| `composeApp/src/commonMain/kotlin/feature/health/list/presentation/HealthListComponent.kt` | Health list component (replaces HealthViewModel) |
| `composeApp/src/commonMain/kotlin/feature/health/track/presentation/TrackHabitComponent.kt` | Track habit component (replaces TrackHabitViewModel) |
| `composeApp/src/commonMain/kotlin/feature/create/presentation/CreateHabitComponent.kt` | Create habit component (replaces ComposeViewModel) |
| `composeApp/src/commonMain/kotlin/feature/statistics/presentation/StatisticsComponent.kt` | Statistics component (replaces StatisticsViewModel) |
| `composeApp/src/commonMain/kotlin/feature/chat/presentation/ChatComponent.kt` | Chat component (replaces ChatViewModel) |
| `composeApp/src/commonMain/kotlin/feature/profile/ProfileComponent.kt` | Profile tab stack component |
| `composeApp/src/commonMain/kotlin/feature/profile/start/presentation/ProfileStartComponent.kt` | Profile start component (replaces ProfileViewModel) |
| `composeApp/src/commonMain/kotlin/feature/profile/edit/presentation/EditProfileComponent.kt` | Edit profile component (replaces EditProfileViewModel) |
| `composeApp/src/commonMain/kotlin/feature/settings/presentation/SettingsComponent.kt` | Settings component (replaces SettingsViewModel) |
| `composeApp/src/commonMain/kotlin/feature/projects/presentation/ProjectListComponent.kt` | Project list component (replaces ProjectListViewModel) |
| `composeApp/src/commonMain/kotlin/feature/splash/SplashComponent.kt` | Splash component |

## Files to Modify

| File | Change |
|------|--------|
| `gradle/libs.versions.toml` | Add Decompose + Essenty dependency versions and library declarations |
| `composeApp/build.gradle.kts` | Add Decompose dependencies, remove `compose-navigation` and `compose-viewmodel` |
| `composeApp/src/commonMain/kotlin/App.kt` | Replace NavHost with RootContent; accept RootComponent parameter |
| `composeApp/src/androidMain/kotlin/tech/mobiledeveloper/jethabit/app/MainActivity.kt` | Create RootComponent with `defaultComponentContext()` and pass to App |
| `composeApp/src/iosMain/kotlin/main.ios.kt` | Create RootComponent with lifecycle and pass to App |
| `composeApp/src/jvmMain/kotlin/Main.kt` | Create RootComponent with lifecycle and pass to App |
| `composeApp/src/commonMain/kotlin/navigation/MainScreen.kt` | Replace with MainContent.kt or remove entirely |
| `composeApp/src/commonMain/kotlin/di/PlatformSDK.kt` | Expose DI instance for passing to RootComponent |
| `composeApp/src/commonMain/kotlin/di/FeatureModule.kt` | Register Decompose components (if needed) |
| All Screen composables (`DailyScreen.kt`, `DetailScreen.kt`, etc.) | Remove ViewModel instantiation, accept Component as parameter, use `Value.subscribeAsState()` |

## Files to Delete

| File | Reason |
|------|--------|
| `composeApp/src/commonMain/kotlin/base/BaseViewModel.kt` | Replaced by Decompose components |
| `composeApp/src/commonMain/kotlin/feature/daily/presentation/DailyViewModel.kt` | Replaced by DailyListComponent |
| `composeApp/src/commonMain/kotlin/feature/detail/presentation/DetailViewModel.kt` | Replaced by DetailComponent |
| `composeApp/src/commonMain/kotlin/feature/health/list/presentation/HealthViewModel.kt` | Replaced by HealthListComponent |
| `composeApp/src/commonMain/kotlin/feature/health/track/presentation/TrackHabitViewModel.kt` | Replaced by TrackHabitComponent |
| `composeApp/src/commonMain/kotlin/feature/create/presentation/ComposeViewModel.kt` | Replaced by CreateHabitComponent |
| `composeApp/src/commonMain/kotlin/feature/statistics/presentation/StatisticsViewModel.kt` | Replaced by StatisticsComponent |
| `composeApp/src/commonMain/kotlin/feature/chat/presentation/ChatViewModel.kt` | Replaced by ChatComponent |
| `composeApp/src/commonMain/kotlin/feature/profile/start/presentation/ProfileViewModel.kt` | Replaced by ProfileStartComponent |
| `composeApp/src/commonMain/kotlin/feature/profile/edit/presentation/EditProfileViewModel.kt` | Replaced by EditProfileComponent |
| `composeApp/src/commonMain/kotlin/feature/settings/presentation/SettingsViewModel.kt` | Replaced by SettingsComponent |
| `composeApp/src/commonMain/kotlin/feature/projects/presentation/ProjectListViewModel.kt` | Replaced by ProjectListComponent |
| `composeApp/src/commonMain/kotlin/navigation/AppScreens.kt` | Navigation handled by Decompose configs |
| `composeApp/src/commonMain/kotlin/navigation/MainScreen.kt` | Replaced by MainContent |
| `composeApp/src/commonMain/kotlin/di/Inject.kt` | Service locator no longer needed |
| Legacy ViewModels in `screens/` package (if unused) | Cleanup |

## Detailed Implementation Approach

### Phase 1: Add Dependencies
1. Add to `libs.versions.toml`:
   - `decompose = "3.2.0"` (or latest stable)
   - `essenty = "2.2.0"` (or compatible version)
   - Library entries for `decompose`, `decompose-compose`, `essenty-lifecycle-coroutines`
2. Update `build.gradle.kts`:
   - Add Decompose and Essenty dependencies to `commonMain`
   - Keep `compose-navigation` and `compose-viewmodel` temporarily during migration
   - Add `kotlinx-serialization` plugin if not already applied (needed for `@Serializable` configs)

### Phase 2: Create Root Component Infrastructure
1. Create `RootComponent` with `Child Stack` managing Splash/Main/Create
2. Create `RootContent.kt` composable that renders the stack using `Children`
3. Create `SplashComponent` (simple component that triggers navigation to Main)

### Phase 3: Create Main Component with Bottom Nav
1. Create `MainComponent` managing 5 tab stacks
2. Each tab is a separate `Child Stack` (using `childStack` with different keys)
3. Create `MainContent.kt` with `BottomNavigation` rendering active tab's stack
4. Tab icons/titles from existing `AppScreens` enum (preserve in MainComponent)

### Phase 4: Migrate Each Feature's ViewModel → Component
For each ViewModel:
1. Create corresponding Component class implementing `ComponentContext by componentContext` and `DIAware`
2. Replace `Inject.instance<T>()` with `by di.instance<T>()`
3. Replace `viewModelScope` with Essenty `coroutineScope(Dispatchers.Main)`
4. Replace `MutableStateFlow` with `MutableValue` / `Value`
5. Replace `SharedFlow<Action?>` with callback lambdas passed via constructor (e.g., `onHabitSelected: (String) -> Unit`)
6. Keep the same State and Event sealed classes
7. Update corresponding Screen composable to accept Component and use `component.state.subscribeAsState()`

### Phase 5: Platform Entry Points
1. **Android** (`MainActivity.kt`): Use `defaultComponentContext()` extension to create `ComponentContext`, instantiate `RootComponent`
2. **iOS** (`main.ios.kt`): Create lifecycle, create `ComponentContext`, instantiate `RootComponent`
3. **Desktop** (`Main.kt`): Use `LifecycleRegistry` + `defaultComponentContext()` or manual setup

### Phase 6: DI Migration
1. Expose `DI` instance from `PlatformSDK` (not just `DirectDI`)
2. Pass `DI` to `RootComponent` constructor
3. Each parent component passes `di` to child components
4. Remove `Inject.kt` service locator
5. Components implement `DIAware` and use `by di.instance()` for dependency resolution

### Phase 7: Cleanup
1. Delete all old ViewModel files
2. Delete `BaseViewModel.kt`
3. Remove `compose-navigation` and `compose-viewmodel` dependencies
4. Delete old navigation files (`AppScreens.kt`, old `MainScreen.kt`)
5. Remove `LocalNavHost` CompositionLocal

## Acceptance Criteria

1. All screens render and function identically to the current implementation
2. Navigation between screens works correctly (forward, back)
3. Bottom navigation preserves per-tab navigation state (nested stacks)
4. Splash screen shows and transitions to Main
5. Create habit flow works from root level
6. Detail screens receive correct parameters (habitId, type, etc.)
7. No `androidx.lifecycle.ViewModel` or `NavHost` imports remain in commonMain
8. `compose-navigation` and `compose-viewmodel` dependencies are removed
9. All components use Kodein `DIAware` pattern (no `Inject.instance()` calls)
10. State is exposed via Decompose `Value<State>`
11. Coroutine scopes are properly managed via Essenty lifecycle
12. App compiles and runs on Android, iOS, and Desktop

## Edge Cases and Risks

### Risks
- **Large migration surface**: 12+ ViewModels and all navigation in one pass. High risk of regressions.
- **Decompose version compatibility**: Must ensure Decompose version is compatible with the Kotlin 2.0.0 and Compose 1.6.10 versions used.
- **Serialization of navigation configs**: All `Config` classes must be `@Serializable`. If configs contain non-serializable types, custom serializers may be needed.
- **State restoration**: Decompose handles state saving via serializable configs. Current ViewModels don't save state — this is an improvement but needs careful config design.
- **Bottom nav state preservation**: Maintaining multiple `Child Stack` instances for tabs requires careful key management to avoid conflicts.

### Edge Cases
- **Deep links**: Current app doesn't appear to use deep links, so not a concern.
- **Back button handling**: Decompose's `Child Stack` handles back navigation. Need to ensure proper back behavior on Android (pop within tab before switching tabs).
- **Concurrent navigation**: Ensure rapid tab switching doesn't cause race conditions in stack management.
- **Configuration changes on Android**: Decompose components survive config changes via `StateKeeper` — must ensure this is properly set up.
- **Process death on Android**: `StateKeeper` should persist navigation state. Configs must be serializable.

## Out of Scope

- Adding new features or changing existing feature behavior
- Migrating to Material 3
- Adding unit tests for components (can be done separately)
- Changing the DI module structure (only changing how DI is accessed)
- Refactoring domain/data layers
- Adding deep link support
- Adding animations/transitions between screens (preserve current behavior)
- Migrating `SettingsEventBus` or theming infrastructure
- JS target support (already disabled in current build)
