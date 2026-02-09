# 2026-02-09-no_magic_numbers

- **Rule:** no_magic_numbers
- **Type:** add
- **Created by:** bob298@yandex.ru
- **Created at:** 2026-02-09T12:00:00Z
- **Finalized at:** 2026-02-09T09:57:42Z
- **Result:** ACCEPTED (Yes: 2, No: 0, Required: 2)

## Change

Add rule to forbid magic numbers and hardcoded constants in business logic

Detect numeric literals (excluding 0, 1, -1) and hardcoded string constants
used directly in business logic Kotlin files. Constants should be extracted
to companion objects or top-level const val declarations.

## Reason

Magic numbers reduce code readability and make maintenance harder

## Impact

All Kotlin files in shared/ and composeApp/ will be checked for magic numbers
