# Spec: Add VK ID Login

## Summary

Add VK ID authentication to JetHabit using the official VK ID native SDKs for Android and iOS. The login replaces the existing `LoginPrompt` component in the profile screen. User data (VK ID, name, avatar URL, email) is stored locally using the existing Room database. The authentication layer should be architected for extensibility so additional providers can be added in the future.

## Requirements (from interview)

| Question | Answer |
|----------|--------|
| Target platforms | Android and iOS |
| SDK approach | VK ID SDK (official native SDKs) |
| Backend | No backend — store VK user info and tokens locally on device |
| User data to fetch | Standard: VK user ID, display name, avatar URL, email |
| UI placement | Replace the existing `LoginPrompt` directly in the profile screen |
| Post-login UI | Show VK user avatar, name, and a logout option in place of `LoginPrompt` |
| Error handling | Show a toast/snackbar with error message; keep `LoginPrompt` visible |
| Extensibility | Architect for future auth providers (Google, Apple, etc.) |

## Architecture Overview

### Current Architecture

- **DI**: Kodein
- **Database**: Room (KMP) with `UserProfile` entity (id, name, email, phoneNumber, avatarUri)
- **UI pattern**: MVI with `BaseViewModel<State, Action, Event>`
- **Platform abstraction**: `expect/actual` classes via `PlatformConfiguration`
- **Navigation**: Jetpack Navigation Compose

### Proposed Architecture

```
commonMain/
  core/auth/
    AuthProvider.kt          -- interface for auth providers (extensible)
    AuthResult.kt            -- sealed class for auth results
    AuthRepository.kt        -- manages auth state, delegates to providers
  feature/profile/
    start/
      presentation/ProfileViewModel.kt  -- modified: add login/logout events
      ui/models/ProfileViewState.kt      -- modified: add isLoggedIn, authProvider fields
      ui/models/ProfileEvent.kt          -- modified: add VkLoginClicked, LogoutClicked
      ui/models/ProfileAction.kt         -- modified: add LaunchVkLogin, ShowError
      ui/views/ProfileView.kt            -- modified: conditional LoginPrompt vs UserHeader+Logout
      ui/views/VkLoginButton.kt          -- new: VK-branded login button

androidMain/
  core/auth/
    VkAuthProvider.android.kt  -- actual VK ID SDK integration for Android

iosMain/
  core/auth/
    VkAuthProvider.ios.kt      -- actual VK ID SDK integration for iOS
```

## Files to Create

### 1. `commonMain/kotlin/core/auth/AuthProvider.kt`
Extensible interface for authentication providers:
```kotlin
interface AuthProvider {
    val providerName: String
    suspend fun signIn(): AuthResult
    suspend fun signOut()
    fun isSignedIn(): Boolean
}
```

### 2. `commonMain/kotlin/core/auth/AuthResult.kt`
Sealed class representing authentication outcomes:
```kotlin
sealed class AuthResult {
    data class Success(
        val userId: String,
        val displayName: String,
        val email: String?,
        val avatarUrl: String?
    ) : AuthResult()
    data class Error(val message: String) : AuthResult()
    data object Cancelled : AuthResult()
}
```

### 3. `commonMain/kotlin/core/auth/AuthRepository.kt`
Repository that manages auth state and delegates to the active provider. Persists auth state (provider name + logged-in flag) to the Room database or a simple key-value store. On login success, writes user profile data to `UserProfileDao`.

### 4. `commonMain/kotlin/core/auth/VkAuthProvider.kt`
`expect` class for the VK ID SDK integration:
```kotlin
expect class VkAuthProvider : AuthProvider
```

### 5. `androidMain/kotlin/core/auth/VkAuthProvider.android.kt`
`actual` implementation using the VK ID SDK for Android:
- Initialize VKID with the app's client ID
- Launch the VK ID OneTap login flow
- Extract user data from the VK ID token/session
- Handle cancellation and errors

### 6. `iosMain/kotlin/core/auth/VkAuthProvider.ios.kt`
`actual` implementation using the VK ID SDK for iOS:
- Initialize VKID via the iOS SDK (likely via Kotlin/Native interop or a Swift wrapper)
- Launch the VK ID login flow
- Extract user data from the VK ID response
- Handle cancellation and errors

### 7. `commonMain/kotlin/feature/profile/start/ui/views/VkLoginButton.kt`
A VK-branded login button composable following VK ID brand guidelines (blue background, VK logo).

## Files to Modify

### 1. `composeApp/build.gradle.kts`
- Add VK ID SDK dependency for Android (`com.vk.id:vkid` or equivalent)
- Add VK ID SDK for iOS via CocoaPods or SPM configuration
- Add any needed Kotlin/Native interop configuration

### 2. `core/database/entity/UserProfile.kt`
Add fields for auth provider tracking:
- `vkId: String?` — VK user ID (nullable for non-VK users)
- `authProvider: String?` — which provider was used ("vk", null for local)

### 3. `core/database/dao/UserProfileDao.kt`
Add query methods:
- `clearProfile()` — for logout functionality

### 4. `feature/profile/start/ui/models/ProfileViewState.kt`
Add fields:
- `isLoggedIn: Boolean = false`
- `authProvider: String? = null`

### 5. `feature/profile/start/ui/models/ProfileEvent.kt`
Add events:
- `VkLoginClicked` — user taps VK login button
- `LogoutClicked` — user taps logout
- `LoginResult(result: AuthResult)` — callback from auth flow

### 6. `feature/profile/start/ui/models/ProfileAction.kt`
Add actions:
- `LaunchVkLogin` — trigger platform-specific VK login flow
- `ShowError(message: String)` — show toast/snackbar

### 7. `feature/profile/start/presentation/ProfileViewModel.kt`
- Inject `AuthRepository`
- Handle `VkLoginClicked`: delegate to `AuthRepository` / `VkAuthProvider`
- Handle `LogoutClicked`: clear auth state, reset profile
- On login success: update `UserProfileDao` with VK user data
- On login failure: emit `ShowError` action

### 8. `feature/profile/start/ui/views/ProfileView.kt`
- When not logged in: show `VkLoginButton` (replacing `LoginPrompt`)
- When logged in: show `UserHeader` with avatar loaded from URL + a "Logout" button
- Handle `ShowError` action with a `Snackbar`

### 9. `feature/profile/start/ui/ProfileScreen.kt`
- Pass `AuthRepository` / `VkAuthProvider` to the ViewModel
- Handle `LaunchVkLogin` action (platform-specific login trigger)

### 10. `di/FeatureModule.kt` (or new `di/AuthModule.kt`)
- Register `VkAuthProvider` and `AuthRepository` in the Kodein DI graph

### 11. `di/PlatformConfiguration.kt`
- May need to expose Android `Activity` reference for VK SDK initialization (already has `activity` on Android)

### 12. Android-specific files
- `AndroidManifest.xml`: Add VK SDK activity/intent-filter declarations if required
- `build.gradle.kts`: Add VK SDK Maven repository if not already present

### 13. iOS-specific files
- `Info.plist`: Add VK app scheme for deep linking if required
- Podfile or Package.swift: Add VKID SDK dependency

## Detailed Implementation Approach

### Phase 1: Core Auth Abstraction (commonMain)
1. Create `AuthProvider` interface, `AuthResult` sealed class
2. Create `AuthRepository` that manages auth state
3. Add `vkId` and `authProvider` fields to `UserProfile` entity (Room migration)
4. Add `clearProfile()` to `UserProfileDao`
5. Register auth components in Kodein DI

### Phase 2: Android VK ID SDK Integration
1. Add VK ID SDK dependency to `build.gradle.kts`
2. Configure VK app in the VK developer console (app ID, redirect URI)
3. Implement `VkAuthProvider.android.kt` using VKID SDK
4. Update `AndroidManifest.xml` with required VK SDK entries
5. Wire up the Activity result handling for the VK login flow

### Phase 3: iOS VK ID SDK Integration
1. Add VKID iOS SDK dependency (CocoaPods/SPM)
2. Implement `VkAuthProvider.ios.kt` with Kotlin/Native interop
3. Update `Info.plist` with VK URL scheme
4. Handle the iOS-specific auth callback flow

### Phase 4: Profile UI Changes
1. Create `VkLoginButton` composable
2. Modify `ProfileView` to conditionally show login vs logged-in state
3. Add logout button below UserHeader when logged in
4. Add Snackbar for error display
5. Update `ProfileViewModel` with login/logout event handling
6. Update `ProfileViewState`, `ProfileEvent`, `ProfileAction`

## Acceptance Criteria

1. **Android login**: User taps VK login button on profile screen -> VK ID SDK login flow opens -> on success, profile shows VK user name, avatar, and email
2. **iOS login**: Same flow as Android using iOS VK ID SDK
3. **Logged-in state**: After login, the VK login button is replaced with user profile info (avatar, name) and a logout option
4. **Logout**: User taps logout -> profile reverts to showing the VK login button, user data is cleared
5. **Error handling**: If VK login fails or is cancelled, a snackbar with an appropriate message appears; the login button remains visible
6. **Persistence**: Login state survives app restart (user stays logged in until explicit logout)
7. **Extensibility**: Adding a new auth provider (e.g., Google) requires only implementing `AuthProvider` and registering it — no changes to the profile UI logic beyond adding a button
8. **Non-VK platforms**: The app continues to compile and run on JVM, JS, and macOS without VK login (the VK login button is hidden or not shown on unsupported platforms)

## Edge Cases and Risks

### Edge Cases
- **Token expiration**: VK ID tokens may expire; the app should handle re-authentication gracefully (show login button again if token is invalid)
- **Network offline**: VK login requires network; show appropriate error message
- **User cancels login**: Must be handled gracefully (distinct from error)
- **Profile data conflict**: If user had manually entered profile data before VK login, VK data should overwrite it
- **Multiple login/logout cycles**: Ensure clean state transitions

### Risks
- **VK ID SDK KMP compatibility**: The VK ID SDK is Android-native (Java/Kotlin) and iOS-native (Swift). Kotlin/Native interop with the iOS SDK may require a Swift wrapper or bridging header, adding complexity
- **Room database migration**: Adding new columns to `UserProfile` requires a Room migration strategy
- **App registration**: Requires a VK developer account and app registration to obtain client IDs. This is a prerequisite before implementation can begin
- **VK SDK version stability**: VK SDK APIs may change; pin to a stable version
- **iOS interop complexity**: The iOS VK ID SDK is in Swift; calling it from Kotlin/Native may require `cinterop` definitions or a thin Objective-C/Swift bridge

## Out of Scope

- **Backend server integration**: No server-side token validation or session management
- **Other auth providers**: Only VK ID is implemented (architecture supports future providers but none are built)
- **Data sync**: No syncing of habits/data between devices using VK account
- **JVM/JS/macOS VK login**: VK login is only available on Android and iOS
- **VK API calls**: No fetching of VK social data beyond basic profile info (friends, posts, etc.)
- **Account linking**: No merging of local profile with VK profile data in complex scenarios
- **Automated tests**: Test strategy is not defined in this scope (can be a follow-up)
