# Start here

## What works

Faveit is a working native Android vertical slice for preference recall. A user
can choose one favorite and start immediately, browse all eight categories,
search “In N Out,” add “In-N-Out” with one tap, see confirmation only after the
local repository reflects the save, recreate the Activity without losing it,
and recall it from Restaurants. Favorites can be renamed, moved, visually
restyled, reset, removed, and rediscovered. Weekly local reminders are explicit
opt-in.

The 96-item bundled catalog, preferences, overrides, and reminder state are
local. The app has no account, backend, ads, analytics, tracking SDK, external
catalog, or INTERNET permission.

## How to run it

For the shortest phone path and a focused three-minute trial, use
[INSTALL_ON_PHONE.md](INSTALL_ON_PHONE.md).

With JDK 17 and Android SDK 36 configured:

~~~bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
~~~

Current debug APK:
app/build/outputs/apk/debug/app-debug.apk (20,163,550 bytes)

SHA-256:
b1ee0e323d7c13c7e0823acb5d4d1f533835c11358b9b3d2bc71690ee3d33777

This is a debug-signed prototype, not a production-distribution build.

## What was validated

- A clean matrix passed:
  testDebugUnitTest, assembleDebug, assembleDebugAndroidTest, lintDebug, and
  assembleRelease.
- 23 JVM tests pass across catalog integrity, ranked search, persistence codec
  and projection, notification policy/copy, editor limits, and delight copy.
- Five Android Compose tests compile. On an official API 30 Google APIs guest,
  a complete run passed “OK (5 tests)” in 233.543 seconds. It covered first-run
  setup, search/add, persisted confirmation, Activity recreation, restaurant
  recall, complete local management, reminder controls, and accessibility
  semantics.
- After the later root content-color correction, the core
  setup/search/add/recreate/recall journey passed again on the rebuilt APK.
  Subsequent all-suite attempts were interrupted by guest input-dispatch ANRs
  under unaccelerated CPU emulation; no timing bound was raised and no failed
  attempt was relabeled as passing.
- The app was uninstalled from the guest, freshly installed, launched, and
  manually taken through the one-favorite Start now path.
- lintDebug reports 0 errors and 3 intentional dependency-version warnings;
  the newer lines require API 37/AGP 9.1, while this app targets API 36.
- Release R8, Compose mapping, release lint, resource shrinking, and packaging
  pass. The unsigned release APK is 1,729,780 bytes with SHA-256
  a835421571b8cd8715e01e07876458843ced1f11dbc66bc338b5a27ebbb48b13.
- The final instrumentation APK is 1,062,202 bytes with SHA-256
  e2a0a9cf6ec8b4cb9e32e39a5e3f5d388e0571cdc0ae50485da26ac9d0b0a416.
- APK manifest audit confirms application ID com.faveit.app, minimum API 23,
  target API 36, and no INTERNET permission. WorkManager contributes
  foreground-service, network-state, wake-lock, boot, and notification
  permissions.
- git diff --check passes.

## Visual evidence

Both images are unobscured 1080-by-1920 captures from the clean-installed API
30 guest and were visually inspected after the contrast correction:

- [First-run setup](docs/agent/delight-install-readiness/evidence/setup-api30.png)
- [All-category Home](docs/agent/delight-install-readiness/evidence/home-api30.png)

The guest had no /dev/kvm; screen capture could starve Android System UI, so
guest error dialogs were hidden only for the final unobscured captures. The
behavioral evidence comes from the separate instrumentation run.

## Known limitations

- A physical-device audit is still needed for real cold-start/search timing,
  TalkBack traversal, large fonts, haptic feel, rotation, and notification
  delivery. The emulator timings are not representative because CPU and GPU
  acceleration were unavailable.
- The catalog is useful but deliberately small and static.
- Visual customization changes name/category/gem style; custom photo picking is
  deferred.
- Reminders use a fixed weekly, inexact WorkManager cadence.
- Release signing and store distribution are not configured.

These are reasonable subjects for the user's prototype trial; none prevents a
short debug install from exercising the core value safely.

## Five things to inspect when you return

1. Time the “In N Out” add on your everyday phone and judge whether persisted
   feedback feels immediate.
2. Open Restaurants during a conversation and judge scan speed and legibility.
3. Decide whether the selected-gem depth, haptic, click, launch mark, and emerald
   confirmation create restrained delight.
4. Customize a favorite, restart, reset, remove, and rediscover it to test the
   local-first mental model.
5. Opt into reminders only if useful and inspect the eventual “Remember X?”
   notification on the physical device.
