// Reproduction case for task c84eb362:
// E1 schedule domain model (ScheduleEntity/ScheduleDao, Migration8to9, DB schema v9)
// exists only on integration/e1-adaptive-reminders (6dc506b7), absent from main.
// This host check asserts the artifacts ARE present on the current working branch.
// EXPECTED (after merge): exit 0. ACTUAL (now, on main): exit 1 — reproduces the bug.
const { execSync } = require('child_process');
const fs = require('fs');

const required = [
  'composeApp/src/commonMain/kotlin/core/database/entity/ScheduleEntity.kt',
  'composeApp/src/commonMain/kotlin/core/database/dao/ScheduleDao.kt',
  'composeApp/src/commonMain/kotlin/core/database/migrations/Migration8to9.kt',
  'composeApp/schemas/core.database.AppDatabase/9.json',
];
const missingFiles = required.filter((f) => !fs.existsSync(f));

let version = null;
const appDb = 'composeApp/src/commonMain/kotlin/core/database/AppDatabase.kt';
const src = fs.readFileSync(appDb, 'utf8');
const m = src.match(/version\s*=\s*(\d+)/);
if (m) version = Number(m[1]);

let migrationRegistered = false;
try {
  const builders = execSync(
    "grep -rl 'Migration8to9' composeApp/src/*/kotlin/core/database/DatabaseBuilder.kt || true",
    { encoding: 'utf8' },
  ).trim();
  migrationRegistered = builders.length > 0;
} catch (e) { /* grep miss */ }

const problems = [];
if (missingFiles.length) problems.push('missing files: ' + missingFiles.join(', '));
if (version !== 9) problems.push(`AppDatabase version = ${version}, expected 9`);
if (!migrationRegistered) problems.push('Migration8to9 not registered in any DatabaseBuilder.kt');

if (problems.length) {
  console.error('REPRO FAIL (bug present): ' + problems.join('; '));
  process.exit(1);
}
console.log('PASS: E1 schedule DB layer present (entity, dao, migration 8to9, schema v9, registered)');
