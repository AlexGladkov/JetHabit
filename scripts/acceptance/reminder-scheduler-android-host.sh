#!/usr/bin/env bash
set -euo pipefail
S=emulator-5554
PKG=com.mobiledeveloper.playzone_mobile.android.debug
ACT=tech.mobiledeveloper.jethabit.app.MainActivity
test "$(adb devices | awk 'NR>1 && $2=="device"{print $1}')" = "$S"
test "$(adb -s "$S" get-state)" = device
test "$(adb -s "$S" shell getprop ro.build.version.sdk | tr -d '\r')" -ge 33
./gradlew --no-daemon :composeApp:assembleDebug :composeApp:assembleDebugAndroidTest
adb -s "$S" install --no-streaming -r -t composeApp/build/outputs/apk/debug/composeApp-debug.apk | grep -Fx Success
OUT="$(adb -s "$S" shell am start -W -S -n "$PKG/$ACT" | tr -d '\r')"
printf '%s\n' "$OUT" | grep -Fx 'Status: ok'
timeout 30 bash -ceu "until adb -s $S shell pidof $PKG >/dev/null 2>&1; do sleep 1; done"
rm -rf composeApp/build/outputs/androidTest-results/connected/debug
ANDROID_SERIAL="$S" ./gradlew --no-daemon :composeApp:connectedDebugAndroidTest
python3 - <<'PY'
from pathlib import Path
import xml.etree.ElementTree as E
files=list(Path('composeApp/build/outputs/androidTest-results/connected/debug').rglob('TEST-*.xml'))
assert files
roots=[E.parse(p).getroot() for p in files]
text=' '.join(E.tostring(r,encoding='unicode') for r in roots)
required=['ReminderSchedulerAndroidTest','BootResyncAndroidTest','NotificationSecurityAndroidTest']
assert all(name in text for name in required)
assert sum(int(r.get('failures',0))+int(r.get('errors',0)) for r in roots)==0
PY
echo REMINDER_SCHEDULER_ANDROID_OK
