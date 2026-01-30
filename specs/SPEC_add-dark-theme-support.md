# Specification: Add Dark Theme Support with Persistence

## Overview
Implement persistent storage for user theme preferences including dark mode, color style, text size, padding size, and corner style. The UI for theme selection already exists but settings are currently stored only in memory and reset on app restart.

## Current State
- ✅ Dark theme UI implementation complete (5 color styles with light/dark variants)
- ✅ Theme toggle UI in Settings screen
- ✅ SettingsEventBus for in-memory state management
- ✅ MainTheme composable with darkTheme parameter
- ❌ No persistence layer for user settings
- ❌ Settings reset to defaults on app restart

## Requirements

### 1. Database Schema
Create a new `UserSettings` entity to store user preferences:
- `id` (Int, primary key, autoincrement)
- `isDarkMode` (Boolean)
- `style` (String) - stores JetHabitStyle enum name
- `textSize` (String) - stores JetHabitSize enum name
- `paddingSize` (String) - stores JetHabitSize enum name
- `cornerStyle` (String) - stores JetHabitCorners enum name

### 2. Data Access Layer
Create `UserSettingsDao` interface with methods:
- `getSettings(): UserSettings?` - retrieve current settings
- `insertOrUpdateSettings(settings: UserSettings)` - upsert settings
- `observeSettings(): Flow<UserSettings?>` - observe settings changes

### 3. Repository Layer
Create `UserSettingsRepository` to:
- Provide abstraction over DAO operations
- Convert between database entities and domain models
- Handle default settings initialization

### 4. Database Migration
Update `AppDatabase`:
- Increment database version from 3 to 4
- Add migration strategy to create `user_settings` table
- Initialize with current default settings

### 5. SettingsEventBus Integration
Update `SettingsEventBus` to:
- Accept `UserSettingsRepository` as dependency
- Load settings from database on initialization
- Persist all settings changes to database immediately
- Maintain current in-memory StateFlow for reactive UI updates

### 6. App Initialization
Update `App.kt` to:
- Initialize `UserSettingsRepository` via dependency injection
- Pass repository to `SettingsEventBus`
- Load persisted settings on app startup

### 7. Dependency Injection
Update Kodein configuration to:
- Register `UserSettingsRepository`
- Provide database instance to repository

## Technical Constraints
- Must maintain compatibility with existing Kotlin Multiplatform architecture
- Use Room database (already in use for other entities)
- Follow existing MVVM patterns in the codebase
- No breaking changes to public APIs
- Settings must persist across app restarts on all platforms

## Testing Scenarios
1. Change theme from light to dark - verify persistence after app restart
2. Change color style - verify persistence after app restart
3. Change text/padding size - verify persistence after app restart
4. Change corner style - verify persistence after app restart
5. First app launch - verify default settings are applied
6. Multiple rapid setting changes - verify all changes are persisted

## Success Criteria
- User theme preferences persist across app restarts
- No performance degradation in theme switching
- Settings load correctly on app startup
- All existing theme functionality continues to work
- Database migration executes without data loss

## Files to Modify
1. `composeApp/src/commonMain/kotlin/data/database/models/UserSettings.kt` (new)
2. `composeApp/src/commonMain/kotlin/data/database/dao/UserSettingsDao.kt` (new)
3. `composeApp/src/commonMain/kotlin/data/database/AppDatabase.kt`
4. `composeApp/src/commonMain/kotlin/data/features/settings/UserSettingsRepository.kt` (new)
5. `composeApp/src/commonMain/kotlin/data/features/settings/SettingsEventBus.kt`
6. `composeApp/src/commonMain/kotlin/App.kt`
7. `composeApp/src/commonMain/kotlin/di/Injection.kt`

## Implementation Order
1. Create database entity and DAO
2. Update database version and migration
3. Create repository layer
4. Update dependency injection
5. Integrate with SettingsEventBus
6. Update App initialization
7. Test persistence across platforms
