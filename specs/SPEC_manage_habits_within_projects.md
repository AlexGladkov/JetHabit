# Specification: Manage Habits Within Projects

## Summary

Add the ability to create **projects** (e.g., "Hobby", "Work", "Life") and assign habits to them. Projects act as categories that help users organise their habits into meaningful groups. The feature introduces a new `ProjectEntity` in the database, a `projectId` foreign-key column on `HabitEntity`, full CRUD for projects (create, rename, change colour, delete), a project picker during habit creation/editing, and a project filter in the Daily view.

Habit-to-project assignment is **optional** — existing habits remain unassigned, and unassigned habits continue to appear in every filter context.

---

## Motivation

Users accumulate many habits over time. Without grouping, the Daily view becomes a flat, undifferentiated list. Projects let users focus on one life area at a time (e.g., show only "Work" habits during work hours) while still being able to see everything via an "All" filter.

---

## Architecture Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| Project assignment | Optional (nullable `projectId`) | Backwards-compatible; no data migration needed for existing habits |
| Project attributes | Name + Colour | Visually distinguishable; keeps scope manageable |
| Daily view filtering | Horizontal chip bar | Matches modern mobile UX; non-destructive (shows "All" by default) |
| Project management UI | Profile section | Avoids adding a bottom-nav tab; Profile already has Settings/Edit patterns |
| Database migration | Version 7 → 8 | Additive: new table + new nullable column |

---

## Data Layer

### New Entity: `ProjectEntity`

```kotlin
// feature/projects/data/ProjectEntity.kt
@Entity
data class ProjectEntity(
    @PrimaryKey
    val id: String,          // UUID
    val title: String,       // User-visible name, e.g. "Work"
    val colorHex: String     // Hex colour string, e.g. "#FF5722"
)
```

### New DAO: `ProjectDao`

```kotlin
// feature/projects/data/ProjectDao.kt
@Dao
interface ProjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: ProjectEntity)

    @Query("SELECT * FROM ProjectEntity ORDER BY title ASC")
    suspend fun getAll(): List<ProjectEntity>

    @Query("SELECT * FROM ProjectEntity WHERE id = :id")
    suspend fun getById(id: String): ProjectEntity?

    @Query("DELETE FROM ProjectEntity WHERE id = :id")
    suspend fun deleteById(id: String)
}
```

### Modified Entity: `HabitEntity`

Add a nullable `projectId` column:

```kotlin
@Entity
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val isGood: Boolean,
    val startDate: String,
    val endDate: String,
    val daysToCheck: String,
    val type: HabitType = HabitType.REGULAR,
    val measurement: Measurement = Measurement.KILOGRAMS,
    val projectId: String? = null          // <-- NEW
)
```

### Modified DAO: `HabitDao`

Add a query to filter habits by project:

```kotlin
@Query("SELECT * FROM HabitEntity WHERE projectId = :projectId")
suspend fun getByProject(projectId: String): List<HabitEntity>
```

### Database Migration

- Bump `AppDatabase` version from **7 → 8**.
- Add `ProjectEntity::class` to the `@Database(entities = [...])` list.
- Add an `AutoMigration(from = 7, to = 8)` or manual migration that:
  1. Creates the `ProjectEntity` table.
  2. Adds `projectId TEXT DEFAULT NULL` column to `HabitEntity`.

---

## Feature Module: `feature/projects/`

Follow the established feature structure:

```
feature/projects/
├── data/
│   ├── ProjectEntity.kt
│   └── ProjectDao.kt
├── domain/
│   ├── CreateProjectUseCase.kt
│   ├── GetAllProjectsUseCase.kt
│   ├── UpdateProjectUseCase.kt
│   └── DeleteProjectUseCase.kt
├── presentation/
│   ├── ProjectListViewModel.kt
│   └── models/
│       ├── ProjectListEvent.kt
│       ├── ProjectListState.kt
│       └── ProjectListAction.kt
├── ui/
│   ├── ProjectListScreen.kt
│   ├── ProjectListView.kt
│   └── views/
│       ├── ProjectCard.kt
│       └── ProjectEditDialog.kt
└── di/
    └── ProjectModule.kt
```

### Use Cases

| Use Case | Input | Output | Notes |
|----------|-------|--------|-------|
| `CreateProjectUseCase` | title, colorHex | Unit | Generates UUID, inserts entity |
| `GetAllProjectsUseCase` | — | `List<ProjectEntity>` | Sorted alphabetically by title |
| `UpdateProjectUseCase` | id, title, colorHex | Unit | Uses REPLACE strategy |
| `DeleteProjectUseCase` | id | Unit | Deletes project; sets `projectId = null` on habits that referenced it |

### DI: `ProjectModule`

```kotlin
val projectModule = DI.Module("projectModule") {
    bind<ProjectDao>() with singleton {
        instance<AppDatabase>().getProjectDao()
    }
    bind<CreateProjectUseCase>() with provider { CreateProjectUseCase(instance()) }
    bind<GetAllProjectsUseCase>() with provider { GetAllProjectsUseCase(instance()) }
    bind<UpdateProjectUseCase>() with provider { UpdateProjectUseCase(instance()) }
    bind<DeleteProjectUseCase>() with provider { DeleteProjectUseCase(instance(), instance()) }
}
```

Wire into `PlatformSDK.init()` via `featureModule()` imports.

---

## UI Changes

### 1. Project List Screen (Profile section)

- Accessible from Profile → "My Projects" menu item.
- Displays all projects as cards with colour swatch and title.
- FAB to add a new project.
- Tap a project → opens edit dialog (rename, change colour, delete).
- Delete shows a confirmation dialog; warns that habits will become unassigned.

### 2. Project Edit Dialog

- Modal dialog / bottom sheet.
- Fields: title (text input), colour (palette of 8-10 preset colours).
- Save and Cancel buttons.

### 3. Habit Creation — Project Picker

Modify `ComposeView.kt` to add a **project selector** (dropdown or chip row) between the title input and the habit type selector. Shows all projects + "None" option. Default = "None".

Update `ComposeViewState` with:
```kotlin
val selectedProjectId: String? = null
val projects: List<ProjectEntity> = emptyList()
```

Update `ComposeEvent` with:
```kotlin
data class ProjectSelected(val projectId: String?) : ComposeEvent
```

Update `ComposeViewModel` to:
- Load projects on init via `GetAllProjectsUseCase`.
- Pass `selectedProjectId` to `CreateHabitUseCase`.

Update `CreateHabitUseCase.execute()` to accept an optional `projectId: String? = null` parameter and set it on the `HabitEntity`.

### 4. Habit Detail — Project Assignment

Modify `DetailView.kt` to show the current project (or "None") and allow changing it via a dropdown.

Update `DetailViewState` with:
```kotlin
val projectId: String? = null
val projectTitle: String? = null
val projects: List<ProjectEntity> = emptyList()
```

Update `DetailEvent`:
```kotlin
data class ProjectChanged(val projectId: String?) : DetailEvent
```

Update `GetDetailInfoUseCase` return model and `UpdateHabitUseCase` to include `projectId`.

### 5. Daily View — Project Filter Chips

Add a horizontal `LazyRow` of chips at the top of `DailyView.kt`:
- First chip: **"All"** (selected by default, shows all habits).
- Subsequent chips: one per project (showing project name, tinted with project colour).
- Optional final chip: **"Uncategorized"** (shows habits with `projectId == null`), only visible if such habits exist.

Update `DailyViewState`:
```kotlin
val projects: List<ProjectEntity> = emptyList()
val selectedProjectId: String? = null  // null = "All"
```

Update `DailyEvent`:
```kotlin
data class ProjectFilterSelected(val projectId: String?) : DailyEvent
```

Update `DailyViewModel`:
- Load projects on init.
- When a filter chip is tapped, re-fetch habits and filter by `projectId` (in-memory or via DAO query).

Update `GetHabitsForTodayUseCase` to accept an optional `projectId: String? = null` parameter and filter accordingly.

### 6. Navigation

Add a navigation route in the Profile nested graph:

```kotlin
composable(ProfileScreens.Projects.name) {
    ProjectListScreen(navController)
}
```

Add `Projects` to the `ProfileScreens` enum.

### 7. Health View (Tracker Habits)

Add the same project filter chip bar to `HealthScreen` so tracker habits can also be filtered by project.

### 8. Statistics View

Add the same project filter chip bar to `StatisticsScreen` so statistics can be viewed per-project.

---

## Files to Create

| File | Purpose |
|------|---------|
| `feature/projects/data/ProjectEntity.kt` | Room entity for projects |
| `feature/projects/data/ProjectDao.kt` | Room DAO for projects |
| `feature/projects/domain/CreateProjectUseCase.kt` | Create a new project |
| `feature/projects/domain/GetAllProjectsUseCase.kt` | Fetch all projects |
| `feature/projects/domain/UpdateProjectUseCase.kt` | Update project name/colour |
| `feature/projects/domain/DeleteProjectUseCase.kt` | Delete project + nullify habit refs |
| `feature/projects/presentation/ProjectListViewModel.kt` | ViewModel for project list |
| `feature/projects/presentation/models/ProjectListEvent.kt` | User events |
| `feature/projects/presentation/models/ProjectListState.kt` | View state |
| `feature/projects/presentation/models/ProjectListAction.kt` | One-time actions |
| `feature/projects/ui/ProjectListScreen.kt` | Screen composable |
| `feature/projects/ui/ProjectListView.kt` | View composable |
| `feature/projects/ui/views/ProjectCard.kt` | Card composable for a single project |
| `feature/projects/ui/views/ProjectEditDialog.kt` | Dialog for create/edit |
| `feature/projects/di/ProjectModule.kt` | DI bindings |

## Files to Modify

| File | Change |
|------|--------|
| `feature/habits/data/HabitEntity.kt` | Add `projectId: String? = null` field |
| `feature/habits/data/HabitDao.kt` | Add `getByProject(projectId)` query |
| `feature/habits/domain/CreateHabitUseCase.kt` | Accept optional `projectId` parameter |
| `core/database/AppDatabase.kt` | Bump version to 8, add `ProjectEntity`, add `getProjectDao()`, add migration |
| `di/DatabaseModule.kt` | Bind `ProjectDao` |
| `di/FeatureModule.kt` | Import `projectModule`, update `CreateHabitUseCase` binding |
| `feature/create/presentation/ComposeViewModel.kt` | Load projects, handle ProjectSelected event, pass projectId on save |
| `feature/create/presentation/models/ComposeEvent.kt` | Add `ProjectSelected` event |
| `feature/create/presentation/models/ComposeViewState.kt` | Add `selectedProjectId`, `projects` |
| `feature/create/ui/ComposeView.kt` | Add project picker UI |
| `feature/detail/presentation/DetailViewModel.kt` | Load projects, handle ProjectChanged event, save projectId |
| `feature/detail/presentation/models/DetailEvent.kt` | Add `ProjectChanged` event |
| `feature/detail/presentation/models/DetailViewState.kt` | Add `projectId`, `projectTitle`, `projects` |
| `feature/detail/domain/GetDetailInfoUseCase.kt` | Include project info in result |
| `feature/detail/domain/UpdateHabitUseCase.kt` | Accept/persist projectId |
| `feature/detail/ui/DetailView.kt` | Show/change project |
| `feature/daily/presentation/DailyViewModel.kt` | Load projects, handle filter, pass projectId to use case |
| `feature/daily/ui/models/DailyViewState.kt` | Add `projects`, `selectedProjectId` |
| `feature/daily/ui/models/DailyEvent.kt` | Add `ProjectFilterSelected` event |
| `feature/daily/ui/DailyView.kt` | Add filter chip bar UI |
| `feature/daily/domain/GetHabitsForTodayUseCase.kt` | Accept optional `projectId` filter |
| `feature/health/list/presentation/HealthViewModel.kt` | Load projects, apply filter |
| `feature/health/list/presentation/models/HealthViewState.kt` | Add project filter fields |
| `feature/health/list/presentation/models/HealthEvent.kt` | Add project filter event |
| `navigation/MainScreen.kt` | Add Projects route in Profile nav graph |
| `navigation/ProfileScreens.kt` | Add `Projects` enum value |
| `feature/profile/` (profile UI) | Add "My Projects" menu item |

---

## Implementation Approach

### Phase 1: Data Layer
1. Create `ProjectEntity` and `ProjectDao`.
2. Add `projectId` column to `HabitEntity`.
3. Add `getByProject` query to `HabitDao`.
4. Update `AppDatabase` (version 8, migration, new DAO accessor).
5. Wire `ProjectDao` into `DatabaseModule`.

### Phase 2: Domain Layer
6. Create all four project use cases (`Create`, `GetAll`, `Update`, `Delete`).
7. Update `CreateHabitUseCase` to accept `projectId`.
8. Update `GetDetailInfoUseCase` and `UpdateHabitUseCase` for project awareness.
9. Update `GetHabitsForTodayUseCase` to accept optional project filter.
10. Create `ProjectModule` DI and wire into `FeatureModule`.

### Phase 3: Project Management UI
11. Build `ProjectListViewModel` + state/event/action models.
12. Build `ProjectListScreen`, `ProjectListView`, `ProjectCard`, `ProjectEditDialog`.
13. Add navigation route and "My Projects" entry in Profile.

### Phase 4: Habit Creation Integration
14. Update `ComposeViewModel` to load projects and handle selection.
15. Update `ComposeViewState` and `ComposeEvent`.
16. Update `ComposeView` with project picker UI element.

### Phase 5: Habit Detail Integration
17. Update `DetailViewModel`, state, events for project assignment.
18. Update `DetailView` with project display/change UI.

### Phase 6: Daily View Filter
19. Update `DailyViewModel` to load projects and handle filter events.
20. Update `DailyViewState` and `DailyEvent`.
21. Update `DailyView` with horizontal chip bar.

### Phase 7: Health & Statistics Views
22. Apply the same filter chip bar to `HealthScreen` and `StatisticsScreen`.

---

## Acceptance Criteria

1. **Create project**: User can create a project with a name and colour from Profile → My Projects.
2. **Edit project**: User can rename a project and change its colour.
3. **Delete project**: User can delete a project; habits that belonged to it become unassigned. A confirmation dialog is shown.
4. **Assign habit on creation**: During habit creation, user can optionally select a project.
5. **Change habit project**: In habit detail, user can change or remove the project assignment.
6. **Filter daily habits**: Daily view shows a chip bar; tapping a project chip shows only that project's habits. "All" shows everything.
7. **Filter tracker habits**: Health view supports the same project-based filtering.
8. **Filter statistics**: Statistics view supports project-based filtering.
9. **Backwards compatibility**: Existing habits (with `projectId = null`) continue to work. They appear under "All" and optionally "Uncategorized".
10. **Database migration**: Upgrading from DB version 7 to 8 preserves all existing data without loss.
11. **Cross-platform**: All changes work on Android, iOS, and Desktop (all use `commonMain`).

---

## Edge Cases and Risks

### Edge Cases

| Case | Expected Behaviour |
|------|--------------------|
| Delete project with habits | Habits' `projectId` set to `null`; habits remain functional |
| Create habit without project | `projectId` is `null`; habit appears in "All" and "Uncategorized" |
| Filter to empty project | Show "No habits in this project" empty state |
| Project with duplicate name | Allow it (UUID is the key, not title); optionally warn user |
| Very long project name | Truncate with ellipsis in chip bar and cards; no hard limit |
| No projects exist | Chip bar hides entirely; Daily view works as before |
| Rename project | All habits keep their assignment; only display name changes |
| Rapid filter switching | Debounce/cancel previous fetch to avoid stale state |

### Risks

| Risk | Severity | Mitigation |
|------|----------|------------|
| **Database migration failure** | High | Use Room's `AutoMigration` if possible; otherwise write a manual migration with `ALTER TABLE ADD COLUMN` and `CREATE TABLE`. Test on all platforms. Destructive migration is currently enabled but should not be relied on for production. |
| **Performance with many projects** | Low | Projects are lightweight entities; chip bar uses `LazyRow`. DAO queries are indexed by `projectId`. |
| **UI clutter in Daily view** | Medium | Chip bar scrolls horizontally; hides when no projects exist. Keep chip height compact. |
| **Platform-specific Room behaviour** | Medium | Room KMP is still alpha (`2.7.0-alpha03`). Test migration on Android, iOS, and Desktop. `AutoMigration` support may vary. |
| **UUID generation** | Low | Currently uses `java.util.UUID` — this is JVM-only. The project already has a `com.benasher44:uuid` dependency. Use that for cross-platform UUID generation in new code. |
| **Breaking existing tests** | Low | No automated tests found in the repo. Manual regression testing required for existing habit creation and daily view. |
| **Colour accessibility** | Low | Provide preset colours with sufficient contrast. Consider light/dark theme handling for colour swatches. |

---

## Out of Scope (Future Enhancements)

- Reordering projects
- Archiving projects
- Project-level goals or progress tracking
- Nested projects (sub-categories)
- Project sharing or collaboration
- Project icons/emojis (deferred to keep scope manageable)
