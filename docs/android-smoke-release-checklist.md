# Android smoke release checklist

Use this Android smoke as a release gate before publishing an Android release candidate.
Keep it local/manual: do not commit signing artifacts, generated APKs, reports, or logs.

## Emulator profile

Recommended clean emulator profile:

- Device: Pixel 7 or Pixel 8 class phone profile.
- System image: Google APIs, Android 15 / API 35, x86_64 or arm64-v8a matching the host.
- State: cold boot after `-wipe-data` for the release-gate run.
- Network: online, default locale/timezone unless the release specifically targets localization.
- Storage: at least 2 GB free.

Example AVD name used below: `JetHabit_API_35`.

## Commands on JDK 21

Run from the repository root. Set JDK 21 only for this shell/session.

```bash
export JAVA_HOME=/path/to/jdk-21
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew --version # must show JVM 21
```

Build the Android candidate used for smoke:

```bash
./gradlew :composeApp:assembleDebug --stacktrace
```

For signed release-candidate smoke, set the `KEYSTORE_*` variables documented in
`docs/release-checklist.md`, then run:

```bash
./gradlew :composeApp:assembleRelease --stacktrace
```

Start a clean emulator and install the build under test:

```bash
emulator -avd JetHabit_API_35 -wipe-data -no-snapshot-load
adb wait-for-device
adb install -r composeApp/build/outputs/apk/debug/composeApp-debug.apk
```

If testing the signed release APK, install `composeApp/build/outputs/apk/release/composeApp-release.apk` instead.

Optional instrumentation gate when Android tests are available:

```bash
./gradlew :composeApp:connectedDebugAndroidTest --stacktrace
```

## Expected reports/results

- `./gradlew --version` reports JVM 21.
- Build command exits with `BUILD SUCCESSFUL`.
- Debug APK exists at `composeApp/build/outputs/apk/debug/composeApp-debug.apk`, or release APK exists at
  `composeApp/build/outputs/apk/release/composeApp-release.apk` for signed RC smoke.
- Optional connected Android test reports, when run, are generated under:
  - `composeApp/build/reports/androidTests/connected/`
  - `composeApp/build/outputs/androidTest-results/connected/`
- Manual smoke result is recorded in the release notes/checklist with device/API, APK type, commit SHA,
  tester, date, and PASS/FAIL.

## Manual E2E smoke note

On the clean emulator, verify launch, onboarding, create/edit/complete a habit, app restart persistence,
main navigation, and no obvious crash/ANR in logcat. Any failure blocks the Android release gate until fixed
or explicitly waived by the release owner.
