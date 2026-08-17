# Faveit

Faveit is a fast, local-first Android prototype for remembering the things you
already love. It is designed for the moment when someone asks, “Where should we
eat?” and every restaurant name suddenly disappears from your head.

Faveit is intentionally not an engagement feed, marketplace, account system,
or advertising surface. Its small discovery batches are computed locally from
the user’s explicit choices; the user’s own favorites remain the authority.

## What is implemented

- An eight-category catalog with 1,680 bundled, researched choices: 210 specific
  entries and 21 meaningful facets per category.
- A quick first-run picker that begins with 12 popular, deliberately different
  choices and reveals 12-item related/exploratory batches on demand. One
  selection is enough to use `Start now`.
- A home screen where all categories remain visible at once.
- A reusable pre-normalized search index with punctuation-, alias-, and
  diacritic-tolerant lookup across the full bundled catalog.
- One-tap rapid add and remove: searching `In N Out` finds `In-N-Out` as a
  Restaurant; the plus/check control toggles it without opening a form.
- Persistence-confirmed emerald feedback for setup and rapid add, including
  accessible live-region announcements.
- Favorites-first category recall grids with a visually separated discovery
  section. A red corner X removes in one tap without moving the tile; the greyed
  tile can be re-added in place, while the body of a saved tile opens editing.
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

- `tools/catalog/generate_catalog.py` — canonical, validated catalog source.
- `app/src/main/assets/catalog.json` — generated bundled master catalog.
- `app/src/main/java/com/faveit/app/data` — catalog loading, indexed ranked
  search, transparent local discovery, DataStore persistence, and favorite
  resolution.
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
`emoji`, `palette`, optional search `aliases`, `facet`, content `tags`, and a
category-local `popularity` rank. Edit the canonical generator rather than the
generated JSON. The generator and loader enforce uniqueness and category
coverage. Research and curation notes are in
[`docs/catalog_sources.md`](docs/catalog_sources.md).
