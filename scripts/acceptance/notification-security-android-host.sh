#!/usr/bin/env bash
set -euo pipefail
S=emulator-5554; A=(adb -s "$S")
PKG=com.mobiledeveloper.playzone_mobile.android.debug
RUN="$PKG.test/androidx.test.runner.AndroidJUnitRunner"
CLS=feature.reminders.schedule.NotificationSecurityAndroidTest
OUT="$(mktemp)"
cleanup(){ rm -f "$OUT"; adb -s "$S" shell pm clear "$PKG" >/dev/null 2>&1 || :; }
trap cleanup EXIT
test "$("${A[@]}" get-state)" = device
"${A[@]}" shell id | grep -F 'uid=2000(shell)'
./gradlew --no-daemon :composeApp:assembleDebug :composeApp:assembleDebugAndroidTest
"${A[@]}" install --no-streaming -r -t composeApp/build/outputs/apk/debug/composeApp-debug.apk | grep -Fx Success
"${A[@]}" install --no-streaming -r -t composeApp/build/outputs/apk/androidTest/debug/composeApp-debug-androidTest.apk | grep -Fx Success
for METHOD in inProcessSecurityOracle seedExternalAttempt; do
  RESULT="$("${A[@]}" shell am instrument -w -r -e class "$CLS#$METHOD" "$RUN" | tr -d '\r')"
  printf '%s\n' "$RESULT" | grep -F 'OK (1 test)'
done
set +e
"${A[@]}" shell am broadcast -n "$PKG/feature.reminders.schedule.ReminderDeliveryReceiver" -a acceptance.security.EXTERNAL --es reminder_id acceptance.security.external --el trigger_at 4102444800000 -d jethabit://reminder/fixed-invalid-external >"$OUT" 2>&1
RC=$?
set -e
# Android 14 may silently reject a non-exported explicit broadcast and report result=0;
# the read-only verifier below is the authoritative no-delivery/no-mutation oracle.
grep -Eqi 'SecurityException|Security exception|Permission Denial|not exported|not allowed|Broadcast completed: result=0' "$OUT"
test "$RC" -ne 0 || grep -Eqi 'Security exception|Permission Denial|not exported|not allowed|Broadcast completed: result=0' "$OUT"
RESULT="$("${A[@]}" shell am instrument -w -r -e class "$CLS#verifyExternalAttempt" "$RUN" | tr -d '\r')"
printf '%s\n' "$RESULT" | grep -F 'OK (1 test)'
echo NOTIFICATION_SECURITY_ANDROID_OK
