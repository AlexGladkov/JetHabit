# VISION: JetHabit → «Adaptive AI Habit OS»

Долгосрочная (годовая+) продуктовая цель для агента `agladkov:control:project-manager`.
Субъект — этот репозиторий JetHabit (KMP/Compose Multiplatform).

## Северная звезда

Превратить JetHabit из демо-трекера привычек в **интеллектуальную кросс-платформенную
операционную систему привычек**: приложение не просто отмечает галочки, а **понимает
пользователя, адаптируется, коучит, мотивирует и связывает с людьми и здоровьем**.
Работает нативно и одинаково хорошо на Android / iOS / Desktop / Web, с облачной
синхронизацией и оффлайн-first.

Метрика успеха видения: пользователь удерживает привычку в 2–3× дольше за счёт адаптивных
напоминаний, AI-коучинга и социальной подотчётности; приложение — витрина архитектуры KMP.

## Инварианты (не нарушать)

- **Архитектура как в проекте:** feature-sliced (data/domain/presentation/ui/di), MVI
  (state/action/event, `BaseViewModel<State, Action, Event>`), Kodein DI, `lifecycle-viewmodel-compose`
  + Compose Navigation (`navigation-compose`). Новые фичи — тем же паттерном.
  *(NB: KViewModel/Odyssey из старого README НЕ используются — исправлено по recon 2026-07-28.)*
- **Governance:** соблюдать правила `.agreements/` — no_android_imports_in_common,
  no_hardcoded_strings, no_force_unwrap, no_magic_numbers.
- **Кросс-платформа real:** платформо-зависимое — только через `expect/actual`
  (notifications, health, secure storage, widgets). commonMain — максимум логики.
- **Оффлайн-first:** Room (сейчас v8) — источник правды; облако — синхронизация поверх, не замена.
- **Тесты обязательны:** доменная логика (расписания, скоринг, синк-мерж) покрыта юнит-тестами.
  Не «зеленить» подгонкой.
- **CI зелёный на всех таргетах** (Android/JVM/iOS/JS) на каждый PR.
- **git:** дети коммитят в feature-ветки/worktree; push и merge в `main` — только человек.

## Эпики (декомпозируемо, параллелизуемо)

### E1. Adaptive Reminders & Scheduling Engine
Доменная модель расписания (время, дни недели, повтор, тайм-зоны), персистентность (Room),
**локальные push кросс-платформенно** (`expect/actual`: Android `AlarmManager`/`NotificationManager`,
iOS `UNUserNotificationCenter`, Desktop tray, Web Notifications). **Адаптивный тайминг**:
движок сдвигает напоминание к времени, когда пользователь реально выполняет привычку
(анализ истории). Экран настройки (MVI+Compose) в habits-флоу. Юнит-тесты логики расписания.

### E2. AI Habit Coach (развить `feature/chat`)
Довести чат до реального коуча: анализ паттернов выполнения → NL-советы, предиктивные
наджи, разбор срывов, постановка целей. Абстракция LLM-провайдера (`expect/actual` или
общий клиент), безопасное хранение ключей (secure storage per-platform). Онбординг-диалог.

### E3. Gamification & Motivation (развить scoring-логику)
Стрики, XP, уровни, ачивменты, челленджи. Убрать хардкод-константы скоринга (см.
`no_magic_numbers` governance) в конфигурируемую доменную модель. Визуал: прогресс,
бейджи, анимации. Экран достижений.

### E4. Health Integration (развить `feature/health`)
`expect/actual`-мост к HealthKit (iOS) / Google Fit / Health Connect (Android). Корреляция
привычек с метриками здоровья (сон, шаги, ЧСС). Инсайты «привычка X ↔ метрика Y».

### E5. Cloud Sync & Backend
Бэкенд (Spring Boot / Ktor — на выбор архитектора): auth, multi-device sync, конфликт-мерж
(CRDT/last-write-wins по полю). API-контракт (REST/GraphQL). Клиентский sync-слой поверх Room,
оффлайн-очередь. Приватность: E2E где возможно.

### E6. Social & Accountability
Общие проекты привычек, дружеские челленджи, лидерборды, шаринг прогресса. Модерация,
приватность по умолчанию. Поверх E5-бэкенда.

### E7. Advanced Analytics & Insights (развить `feature/statistics`)
Тренды, хитмапы, недельные/месячные отчёты, экспорт (CSV/PDF). Предиктивная аналитика срывов.

### E8. Cross-Platform Polish & Reach
Полный паритет Web + Desktop. Домашние виджеты (`expect/actual`), watchOS/WearOS-комплики,
быстрые действия. Дизайн-система, тёмная тема, локализация (LibRes), a11y.

## Порядок (грубо, PM уточняет recon'ом)

Кварталы-ориентир: Q1 E1+E3 (фундамент вовлечения) → Q2 E2+E7 (интеллект) →
Q3 E5+E4 (облако+здоровье) → Q4 E6+E8 (социальное+охват). Внутри квартала — параллельный
найм спецов (kotlin-specialist, swift-expert, spring-boot-engineer, api-designer,
ui-designer, mobile-developer из глобального реестра voltagents).

## Гейты к человеку

Каждый новый эпик/крупная идея → на одобрение (product_propose) до старта. Готовый результат
эпика (тесты зелёные, сборка ок на таргетах) → product_submit_review. Merge — человек.
