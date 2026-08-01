# Release checklist

Use JDK 21 for all local and CI Gradle release checks. Do not require global host changes; export `JAVA_HOME` only for the shell/session that runs the checklist.

Example macOS/Linux setup:

```bash
export JAVA_HOME=/path/to/jdk-21
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew --version
```

`./gradlew --version` must show JVM 21 before running release checks.

## Secrets

`release.keystore` and other signing artifacts are local files or CI secrets only. Never commit them to git. Keep passwords/aliases in local environment variables or CI secret storage.

If a signing artifact was ever tracked, remove it from the index without deleting the local file:

```bash
git rm --cached release.keystore
```

Android release signing is explicit-only. `:composeApp:assembleRelease` requires all of these environment variables and will not fall back to a repository-root `release.keystore`:

```bash
export KEYSTORE_FILE=/absolute/path/to/release.keystore
export KEYSTORE_PASSWORD=...
export KEY_ALIAS=...
export KEY_PASSWORD=...
```

## Gradle checks

Run only real Gradle task names; `commonTest` is a Kotlin source set, not a Gradle task.

Recommended release foundation checks:

```bash
./gradlew :composeApp:jvmTest --stacktrace
./gradlew :composeApp:jvmJar --stacktrace
./gradlew :composeApp:assembleDebug --stacktrace
./gradlew :composeApp:assembleRelease --stacktrace # requires KEYSTORE_* variables above
./gradlew :composeApp:linkDebugFrameworkIosX64 --stacktrace # macOS/iOS toolchain only
```

For a broader KMP test aggregation, use:

```bash
./gradlew :composeApp:allTests --stacktrace
```

## Remaining manual release gates

- Android emulator smoke: run `docs/android-smoke-release-checklist.md` as the Android release gate.
- iOS simulator smoke: run the release-candidate build on a supported simulator and verify launch, onboarding, core habit flow, persistence after restart, and no obvious runtime failures.

## Release notes / changelog identity

- Use `JetHabit` consistently in release notes, changelog entries, screenshots, and store metadata.
- Do not mention old template/sample names such as PlayZone or JetpackComposeDemo in user-facing release text.
- Do not change Android `applicationId` or iOS bundle identifiers as part of release-note or metadata cleanup.

## Store upload / provisioning

Manually confirm Play Console and App Store Connect upload readiness, bundle identifiers/application IDs, version/build numbers, metadata, screenshots, release tracks, and required declarations.

Keep Android keystore/Play signing and Apple certificates/profiles in CI secret storage or local secure storage only; verify CI/local machines can provision signing without committing credentials or generated signing artifacts.
