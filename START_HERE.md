# Start here

## What works

Faveit is a working native Android vertical slice: first-run favorite selection,
all-category home recall, forgiving search and one-tap add, favorites-first
category grids, local customization/removal/reset, and opt-in weekly local
notifications. The bundled catalog contains 96 entries across Restaurants,
Music, Movies, TV, Books, Games, Activities, and Foods.

## How to run it

Open in Android Studio with JDK 17 and Android SDK 36, then run the `app`
configuration on any API 23+ device. From a configured terminal:

```bash
./gradlew assembleDebug
```

Current debug artifact: `app/build/outputs/apk/debug/app-debug.apk`.
SHA-256: `28bad8a25987284ac3f400c4fd3ca1e3bc4329b749471dd07d894d51988e6010`.

## What was validated

- `testDebugUnitTest`: 11 passing tests covering punctuation/alias/diacritic,
  non-Latin and emoji-only search, supplementary-character name limits,
  malformed and Unicode override persistence, and the 96-item catalog contract.
- `assembleDebug`: passing; installable debug APK produced (about 20 MB).
- `assembleRelease`: passing through R8, Compose mapping, release lint, and
  resource shrinking; an unsigned release APK is produced for signing later.
- `assembleDebugAndroidTest`: passing; the Compose journey test APK and test
  code compile.
- `lintDebug`: passing with 0 errors. Its 3 informational warnings identify
  newer Core/Lifecycle releases that require API 37/AGP 9.1; the app is
  intentionally on the compatible API 36 line.
- JSON syntax, duplicate catalog IDs, category coverage, Gradle wrapper, and
  whitespace checks pass.

The environment had no JDK, Android SDK, `/dev/kvm`, or X11 bridge initially.
The official toolchain was bootstrapped and an API 36 emulator was attempted
with software CPU/GPU rendering. After supplying its missing host X11 library,
the guest launched but remained ADB-offline beyond its startup window because
hardware virtualization is unavailable. Therefore the compiled instrumentation
journey was not executed and screenshots were not fabricated. Run
`./gradlew connectedDebugAndroidTest` on a real device or accelerated emulator
for that final device-level receipt.

## Known limitations

- The master catalog is useful but deliberately small and static.
- Visual customization changes name/category/gem style; custom photo picking is
  deferred.
- Reminders are a fixed weekly cadence and WorkManager timing is inexact by
  design.
- No physical-device performance or accessibility audit has yet been possible.
- Release signing/distribution is not configured; this is a debug prototype.

## Five things to inspect when you return

1. Time the `In N Out` rapid-add journey on your everyday phone.
2. Open Restaurants during a conversation and judge grid scan speed, density,
   and legibility—not just aesthetics.
3. Customize a favorite’s name, category, and gem, restart the app, then reset
   it to verify the local-first mental model.
4. Enable notifications and inspect the permission copy and eventual
   `Remember X?` notification on-device.
5. Review [DECISIONS.md](DECISIONS.md) and [TODO.md](TODO.md) before expanding
   scope, especially the no-sponsored-content and user-authority boundaries.
