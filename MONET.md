---
agents:
  - agladkov:control:*
  - coder
  - general-purpose
  - researcher
  - jethabit:kmp-compose-engineer
  - jethabit:release-build-tester
  - agladkov:testing:android-tester
  - agladkov:testing:ios-tester
  - voltagents:lang:swift-expert
  - voltagents:core:mobile-developer
stack:
  - kotlin
  - kmp
  - compose-multiplatform
  - android
  - ios
  - jvm-desktop
  - gradle-kts
  - room
  - ksp
  - kodein
  - xcode
---

# JetHabit project brain

## Архитектура и стек
JetHabit — Kotlin Multiplatform / Compose Multiplatform приложение. Основной модуль: `composeApp`.
Платформенные source sets: `androidMain`, `iosMain`, `jvmMain`, `jsMain`, `macosMain`; общий код — `commonMain`, тесты — `commonTest`/`androidTest`.
`iosApp` содержит Xcode wrapper. `kotlin-js-store` хранит web-зависимости; JS target в Gradle сейчас ограничен/закомментирован из-за Room.
Ключевые технологии: Kotlin 2.0.0, Compose Multiplatform 1.6.10, Gradle Kotlin DSL, JDK 21, Room + KSP, Kodein DI, kotlinx serialization/coroutines/datetime, Compose Navigation/ViewModel, Coil MP.

## Инварианты
- Общая бизнес/UI-логика — в `composeApp/src/commonMain`; platform-specific API — только в соответствующих `<platform>Main`.
- Не импортировать Android/iOS API в общий код.
- Не трогать `iosApp` и `kotlin-js-store`, если задача явно не про iOS wrapper/web.
- Не менять signing/release keystore/secrets без отдельного согласования.
- Соблюдать `.agreements/rules.yml`: избегать `!!`, hardcoded user-facing strings в Compose UI, магических чисел в бизнес-логике.
- Kotlin стиль из `AGENTS.md`: 4 пробела, explicit return types для public API, KDoc для public классов/функций, `val`/immutable где возможно, строки до 120 символов.

## Правила параллельной работы
- Перед стартом агент объявляет область и файлы, которые планирует менять.
- Разные агенты не редактируют одни и те же файлы/близкие блоки одновременно.
- Изменения должны быть минимальными и scoped to task; без переписывания архитектуры ради локального фикса.
- Если обнаружен конфликт или требуется менять чужую область — остановиться и эскалировать PM/Head of Engineering.

## QA и сборка
Минимальные проверки выбираются по области изменения; green можно заявлять только после фактического запуска:
- Общие/KMP тесты: `./gradlew :composeApp:jvmTest --stacktrace` или `./gradlew :composeApp:allTests --stacktrace`; `commonTest` — source set, не Gradle task.
- Android: `./gradlew :composeApp:assembleDebug --stacktrace`; для UI/smoke нанимать `agladkov:testing:android-tester`.
- JVM/Desktop: `./gradlew :composeApp:jvmJar --stacktrace` (и `:composeApp:jvmRun`, если нужен runtime smoke).
- iOS framework: `./gradlew :composeApp:linkDebugFrameworkIosX64 --stacktrace` на macOS; для Xcode/simulator нанимать `agladkov:testing:ios-tester`.
- Перед релизом: поручить `jethabit:release-build-tester` полный build matrix и собрать отчёт PASS/FAIL/SKIP.

## Hiring guide
- Координация релиза/эпика: `agladkov:control:project-manager`; инженерная ответственность за зелёный билд: `agladkov:control:head-of-engineering`.
- Kotlin/KMP/Compose фичи и фиксы: `jethabit:kmp-compose-engineer`.
- Простые точечные правки: `coder` при явных файлах и критериях готовности.
- Android device/emulator QA: `agladkov:testing:android-tester`.
- iOS build/simulator QA: `agladkov:testing:ios-tester`.
- Release build matrix и диагностика Gradle/Xcode: `jethabit:release-build-tester`.
- Swift/iOS wrapper изменения: `voltagents:lang:swift-expert`.
- Исследования/документация/поиск: `researcher`.
