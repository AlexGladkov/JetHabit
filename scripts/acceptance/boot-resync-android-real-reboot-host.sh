#!/usr/bin/env bash
set -euo pipefail
S=emulator-5554; A=(adb -s "$S")
PKG=com.mobiledeveloper.playzone_mobile.android.debug
ACT=tech.mobiledeveloper.jethabit.app.MainActivity
RUN="$PKG.test/androidx.test.runner.AndroidJUnitRunner"
CLS=feature.reminders.schedule.BootResyncAndroidTest
RID="acceptance.boot.$(date +%s).$$"; AT=$(( $(date +%s)*1000 + 86400000 ))
DIG="$(printf %s "$RID" | sha256sum | awk '{print $1}')"; URI="jethabit://reminder/$DIG"
TMP="$(mktemp -d)"
cleanup(){ rm -rf "$TMP"; adb -s "$S" shell pm clear "$PKG" >/dev/null 2>&1 || :; }
trap cleanup EXIT
test "$("${A[@]}" get-state)" = device
./gradlew --no-daemon :composeApp:assembleDebug :composeApp:assembleDebugAndroidTest
"${A[@]}" install --no-streaming -r -t composeApp/build/outputs/apk/debug/composeApp-debug.apk | grep -Fx Success
"${A[@]}" install --no-streaming -r -t composeApp/build/outputs/apk/androidTest/debug/composeApp-debug-androidTest.apk | grep -Fx Success
# Clear the package's post-install stopped state so Android is allowed to deliver BOOT_COMPLETED.
"${A[@]}" shell monkey -p "$PKG" -c android.intent.category.LAUNCHER 1 >/dev/null
B0="$("${A[@]}" shell cat /proc/sys/kernel/random/boot_id | tr -d '\r')"
SEED="$("${A[@]}" shell am instrument -w -r -e class "$CLS#seedFutureProductionFixtureForHostReboot" -e reminderId "$RID" -e triggerAt "$AT" "$RUN" | tr -d '\r')"
printf '%s\n' "$SEED" | grep -F 'OK (1 test)'
# am instrument force-stops its target when it finishes, which removes that package's
# AlarmManager/PendingIntent projection while leaving the committed reminder record intact.
# A normal launcher start clears only the stopped bit. Application startup does not schedule
# or reconcile reminders; the reboot terminates the process before BOOT_COMPLETED recovery.
START="$("${A[@]}" shell am start -W -n "$PKG/$ACT" | tr -d '\r')"
printf '%s\n' "$START" | grep -Fx 'Status: ok'
"${A[@]}" reboot
timeout 180 adb -s "$S" wait-for-device
timeout 180 bash -ceu "until test \"\$(adb -s $S shell getprop sys.boot_completed 2>/dev/null | tr -d \\\r)\" = 1; do sleep 2; done"
"${A[@]}" shell input keyevent 82 >/dev/null 2>&1 || :
"${A[@]}" shell wm dismiss-keyguard >/dev/null 2>&1 || :
timeout 60 bash -ceu "until adb -s $S shell dumpsys user 2>/dev/null | grep -q RUNNING_UNLOCKED; do sleep 1; done"
B1="$("${A[@]}" shell cat /proc/sys/kernel/random/boot_id | tr -d '\r')"; test -n "$B1"; test "$B1" != "$B0"
# Wait for the OS boot broadcast to reconstruct the alarm before instrumentation can
# force-stop the target process. No activity, scheduler, or receiver is invoked here.
deadline=$((SECONDS+60))
while (( SECONDS < deadline )); do
  "${A[@]}" shell dumpsys alarm >"$TMP/post-alarm"
  "${A[@]}" shell dumpsys activity intents >"$TMP/post-intents"
  if grep -Fq "$AT" "$TMP/post-alarm" && grep -Fq "$URI" "$TMP/post-intents"; then break; fi
  sleep 2
done
python3 - "$TMP/post-alarm" "$TMP/post-intents" "$URI" "$PKG" "$AT" <<'PY'
import sys
alarm,intents=open(sys.argv[1],errors='replace').read(),open(sys.argv[2],errors='replace').read()
uri,pkg,at=sys.argv[3:]
checks={
 'alarm_package': pkg in alarm,
 'alarm_receiver': 'ReminderDeliveryReceiver' in alarm,
 'alarm_epoch': at in alarm,
 'intent_uri': uri in intents,
 'intent_package': pkg in intents,
 'intent_receiver': 'ReminderDeliveryReceiver' in intents,
}
print(checks)
assert all(checks.values()), checks
PY
VERIFY="$("${A[@]}" shell am instrument -w -r -e class "$CLS#verifyFutureFixtureAfterRealBootReadOnly" -e reminderId "$RID" -e triggerAt "$AT" "$RUN" | tr -d '\r')"
printf '%s\n' "$VERIFY" | grep -F 'OK (1 test)'
echo REAL_REBOOT_RESYNC_OK
