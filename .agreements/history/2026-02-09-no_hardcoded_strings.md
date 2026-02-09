# 2026-02-09-no_hardcoded_strings

- **Rule:** no_hardcoded_strings
- **Type:** modify
- **Created by:** bob298@yandex.ru
- **Created at:** 2026-02-09T08:56:54Z
- **Finalized at:** 2026-02-09T08:59:14Z
- **Result:** ACCEPTED (Yes: 2, No: 0, Required: 2)

## Change

Exclude test files from hardcoded string check

Add androidTest and test paths to exclusion

## Reason

Test files use hardcoded strings for UI assertions

## Impact

Only affects test code, production rules unchanged
