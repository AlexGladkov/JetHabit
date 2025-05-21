# AGENTS Instructions

## Project architecture
- This repository contains a Compose Multiplatform project. The core module is `composeApp`.
- Platform specific sources live under `composeApp/src/<platform>Main/` (e.g. `androidMain`, `iosMain`).
- The `iosApp` directory contains the Xcode project for iOS. The `kotlin-js-store` directory only holds web dependencies.
- Keep changes scoped to the relevant module or folder. Avoid touching unrelated modules when fixing or adding functionality.

## Kotlin development rules
- Use **four spaces** for indentation.
- Follow Kotlin naming conventions: `PascalCase` for classes and objects, `camelCase` for functions and variables.
- Provide explicit return types on public functions.
- Document public classes and functions using KDoc comments.
- Prefer immutable data (`val` and data classes) when possible.
- Try to keep line length under **120 characters**.
- Delete unused imports if they exist.

## Working with the project
- Before committing code, run `./gradlew build` to ensure the project compiles.
- Only update `iosApp` or `kotlin-js-store` when a change explicitly targets these folders.
