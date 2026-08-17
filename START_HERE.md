# Start here

## What works

Faveit now ships a researched local catalog of 1,680 concrete choices: exactly
210 in each of Restaurants, Music, Movies, TV, Books, Games, Activities, and
Foods. First-run setup shows 12 popular, deliberately different choices per
category, then adds stable 12-item batches that interleave related picks with
exploration as the user selects favorites.

Search remains immediate and supports one-tap add and one-tap unfavorite.
Setup and category grids provide an independent red X for removal; the removed
tile stays in place, greys out, and can be restored with one tap. Tapping the
body of a saved tile still opens local rename/category/gem customization.

All favorites, overrides, setup state, and reminder state remain local. The app
has no account, backend, ads, analytics, tracking SDK, external catalog, remote
model, or INTERNET permission.

## How to run it

For the shortest phone path and a focused trial, use
[INSTALL_ON_PHONE.md](INSTALL_ON_PHONE.md).

With JDK 17 and Android SDK 36 configured:

~~~bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
~~~

Current debug APK:
`app/build/outputs/apk/debug/app-debug.apk` (20,303,576 bytes)

SHA-256:
`484d1b280384328f569b4246343380139ee6e7ca88a05ef68a5e5628c2e7a92e`

This is a debug-signed prototype, not a production-distribution build.

## What was validated

- The final matrix passed: `testDebugUnitTest`, `assembleDebug`,
  `assembleDebugAndroidTest`, `lintDebug`, and `assembleRelease`.
- All 35 JVM tests passed with zero failures, errors, or skips. They cover the
  1,680-item catalog contract, duplicate/filler regressions, legacy ID and
  visual/alias migration, API-23 glyph policy, owner-scoped compatibility
  recovery, adaptive 6-related/6-exploratory discovery, ranked search,
  persistence, notification behavior, editor limits, and delight copy.
- A reusable full-catalog search index completed a 250-query typing workload in
  0.283 seconds on this host. The test's generous regression ceiling is 1.5
  seconds; this is a host regression signal, not a phone latency claim.
- The generator reproduced exactly 1,680 items, 210 per category, with 21
  facets per category, unique IDs and category-local names, complete metadata,
  no duplicate tags, no rejected filler labels, and only baseline-safe tile
  glyphs. All 14 rejected legacy filler IDs remain hidden runtime
  compatibility records. A direct comparison with the shipped catalog proves
  that all 82 retained items keep all 132 prior aliases.
- On an official API 30 Google APIs guest, one combined invocation passed
  `OK (5 tests)` in 206.068 seconds after all substantive catalog,
  discovery, search, UI, background-projection, and WorkManager lifecycle
  changes. It covered setup, persisted add/remove/re-add, Activity recreation,
  category recall, rename/move/restyle/reset/remove, reminder controls, and
  accessibility semantics. A post-run log audit found no Faveit crash.
- Seven Android tests now compile, including new regressions for discoverable
  setup counts, category pinned-state recreation, and owner-scoped recovery of
  retired favorites. A current runtime attempt could not boot a target: API 36
  requires unavailable KVM, while API 30 software mode exits in the emulator
  launcher because `libX11-xcb.so.1` is absent. This host failure occurred
  before any app assertion and is not represented as a passing run.
- `lintDebug` reports 0 errors and only 3 pre-existing dependency-version
  warnings. The newer dependency lines require API 37/AGP 9.1, while Faveit
  targets API 36.
- Release R8, Compose mapping, resource shrinking, and packaging pass. The
  unsigned release APK is 1,773,688 bytes with SHA-256
  `1664ebae2d327005d820979fda2a24efca5e34b52bf58a5dd565438e2b11c664`.
  The instrumentation APK is 1,083,513 bytes with SHA-256
  `4d8d9007d84f3e6dedbcef63a4ef950858467386b95bb65ef6b744d6efa88fb6`.
- APK manifest audit confirms application ID `com.faveit.app`, minimum API
  23, target API 36, and no INTERNET permission. `git diff --check` passes.

## Visual evidence

These unobscured 1080-by-1920 captures were taken from the API 30 guest and
visually inspected:

- [Adaptive first-run setup](docs/agent/adaptive-catalog/evidence/setup-api30.png)
- [Selected setup tile with red X](docs/agent/adaptive-catalog/evidence/setup-selected-api30.png)
- [All-category Home](docs/agent/adaptive-catalog/evidence/home-api30.png)

The guest had no `/dev/kvm`; its wall times and occasional system-server
failures are not representative of physical-device speed.

## Known limitations

- Physical-device measurement is still needed for representative cold-start
  and search timing, TalkBack traversal, large fonts, haptic feel, rotation,
  and delayed notification delivery.
- The catalog is intentionally broad but U.S.- and English-language-leaning.
  It is static until a versioned regional/update process is built.
- Suggestions are transparent, deterministic content-neighbor grouping, not a
  trained personal model. Quality needs evaluation with consenting users.
- User-created items, custom photos, nested favorites, and family sharing are
  future work; see [docs/roadmap.md](docs/roadmap.md).
- Reminders use a fixed weekly, inexact WorkManager cadence.
- Release signing and store distribution are not configured.

## Five things to inspect when you return

1. Try several obscure and punctuation-heavy searches and confirm the expanded
   catalog still feels instant on your phone.
2. Select a few clearly related favorites during setup, request more picks, and
   judge the related/exploratory balance.
3. Remove and restore a favorite from search, setup, and a category grid; verify
   the controls feel obvious and the tile never runs away.
4. Confirm an existing favorite kept its familiar gem treatment after updating,
   then rename, move, restyle, reset, and restart the app.
5. Scan [docs/catalog_sources.md](docs/catalog_sources.md) and
   [docs/roadmap.md](docs/roadmap.md) for coverage priorities and Bethany's
   nested-favorite/family-network ideas.
