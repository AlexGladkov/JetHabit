# Release identity checklist

Use this checklist before Android or iOS release builds. This document records identity risks only; it does not change Android `applicationId` or iOS bundle identifiers.

## Android

- Launcher label resolves to `JetHabit` via `composeApp/src/androidMain/res/values/strings.xml` and `AndroidManifest.xml` `android:label`.
- Store listing, screenshots, release notes, and privacy text use `JetHabit` consistently.
- Search release artifacts for old sample/template names before upload.
- Verify the current `applicationId` intentionally remains unchanged for this cleanup pass.

## iOS

- `CFBundleDisplayName` and `CFBundleName` in `iosApp/iosApp/Info.plist` are `JetHabit`.
- Xcode archive display name, App Store Connect metadata, screenshots, release notes, and privacy text use `JetHabit` consistently.
- Verify the current bundle identifier intentionally remains unchanged for this cleanup pass.

## Application ID / bundle ID risk audit

Current known identity values:

- Android Gradle `applicationId`: `com.mobiledeveloper.playzone_mobile.android` (not changed here because Gradle files are out of scope).
- iOS bundle identifier: `tech.mobiledeveloper.JetHabit` (not changed here by request).
- Shared pod summary should resolve to `JetHabit` in Gradle/podspec configuration.

Risks if IDs are changed later:

- Android: changing `applicationId` creates a different app for Play Store/users and can break updates, signing continuity, deep links, backups, analytics, push, and billing associations.
- iOS: changing the bundle identifier creates a different App Store app target unless coordinated with provisioning, App Store Connect, entitlements, push, keychain access groups, universal links, and analytics.
- Cross-platform services: OAuth redirect URIs, backend allowlists, crash reporting, push topics, and analytics app registrations may need coordinated migration.

Recommended follow-up:

1. Decide whether visible JetHabit branding is sufficient while keeping existing IDs for continuity.
2. If ID migration is required, plan it as a dedicated release/ops task with store, provisioning, backend, analytics, push, and user-migration validation.
3. Keep store metadata drafts docs-only unless a separate release task approves changing identifiers or store-console data.
