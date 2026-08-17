# Faveit

Faveit is a fast, local-first Android prototype for remembering the things you
already love. It is designed for the moment when someone asks, “Where should we
eat?” and every restaurant name suddenly disappears from your head.

The MVP is intentionally not a recommendation feed, marketplace, account
system, or advertising surface. The user’s own favorites are the authority.

## What is implemented

- An eight-category first-run picker with 96 bundled demonstration entries;
  one selection is enough to use `Start now`, while browsing every category
  remains available.
- A home screen where all categories remain visible at once.
- Punctuation-, alias-, and diacritic-tolerant global search.
- One-tap rapid add: searching `In N Out` finds `In-N-Out` as a Restaurant.
- Persistence-confirmed emerald feedback for setup and rapid add, including
  accessible live-region announcements.
- Favorites-first category recall grids with a visually separated discovery
  section.
- Local favorite removal, renaming, category reassignment, gem-style changes,
  and restoration of catalog defaults.
- Explicitly opt-in, non-annoying weekly local reminders in the form
  `Remember In-N-Out?`.
- Glossy gemstone surfaces rendered by Compose from gradients and facets, with
  press animation, haptics, click feedback, and accessible state semantics. No
  commercial artwork is bundled.

See [START_HERE.md](START_HERE.md) for the current validation receipt and a
short return-to-project guide. See [INSTALL_ON_PHONE.md](INSTALL_ON_PHONE.md)
for the direct phone-install path and a three-minute first-session script.

## Run it

Recommended: open the repository in a current Android Studio release, let it
install Android SDK 36, and run the `app` configuration on an API 23+ device.

Command line prerequisites are JDK 17 and Android SDK 36. Set `sdk.dir` in an
untracked `local.properties`, then run:

```bash
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

Validation commands:

```bash
./gradlew testDebugUnitTest assembleDebug assembleDebugAndroidTest lintDebug
./gradlew connectedDebugAndroidTest  # requires a connected device or emulator
```

## Project map

- `app/src/main/assets/catalog.json` — replaceable bundled master catalog.
- `app/src/main/java/com/faveit/app/data` — catalog loading, ranked search,
  DataStore persistence, and favorite resolution.
- `app/src/main/java/com/faveit/app/ui` — Compose app flow, screens, gem
  components, and visual theme.
- `app/src/main/java/com/faveit/app/notifications` — opt-in WorkManager schedule
  and local notification delivery.
- `app/src/test` — search, persistence-codec, projection, reminder-copy, and
  catalog contract tests.
- `app/src/androidTest` — two end-to-end Compose journeys plus three focused
  setup/accessibility-semantics tests.

## Data and privacy

Faveit has no backend, login, analytics, ads, tracking SDK, `INTERNET`
permission, or external catalog dependency. Favorites and overrides use Preferences
DataStore on the device. Android may include this file in the user’s normal
OS-managed device backup or transfer; Faveit itself sends nothing anywhere.

To replace or extend the prototype catalog, retain the schema in
`catalog.json`: stable `id`, display `name`, category wire name, safe visual
`emoji`, `palette`, and optional search `aliases`. The loader enforces unique
IDs and coverage of all categories.
