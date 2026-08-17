# Decisions

Consequential MVP decisions and ambiguous-spec interpretations are recorded
here so a future maintainer can change them deliberately.

## Product

1. **“Suggests” means recall, not inference, in the MVP.** Search, category
   lookup, and an occasional random reminder surface only items in the user’s
   own favorites. There is no behavioral ranking or recommendation model.
2. **All reminder behavior is opt-in.** Enabling reminders asks for Android’s
   notification permission where required, schedules one reminder per week,
   and delays the first by three days. The worker exits silently when there are
   no favorites or permission has gone away.
3. **Visual customization means local display name, category, and gem style.**
   An image picker was not included because the MVP can satisfy fast visual
   differentiation with safe, generated surfaces and no storage permission or
   image-management flow. Custom images remain a post-MVP option.
4. **The eight major categories are fixed for the MVP.** This guarantees a
   stable, sub-second home scan and a simple catalog contract. User-created
   categories are deferred.
5. **Discoverability never outranks favorites.** A category shows favorites
   first in full color; non-favorites remain in the same view below a clear
   divider and are deliberately muted.
6. **Setup is complete when the user has received value, not when every page
   has been visited.** After one favorite is selected, `Start now` becomes
   available. The eight-category tour remains browsable but is not a toll gate.

## UX and visual system

1. **Search is a full-width field, not a hidden icon.** This is slightly more
   visually expensive but removes a tap from the five-second rapid-add goal.
2. **Home uses 2×4 in portrait and 4×2 in landscape.** All categories remain
   visible without scrolling in either orientation.
3. **Gem visuals are code-generated.** Layered gradients, highlights, borders,
   emoji, press depth, system click sound, and haptics provide identity without
   licensing risk or image decode/network latency.
4. **No timed splash screen.** The Android launch window and the brief
   in-Compose loading state use the same local emerald mark, but both disappear
   as soon as the first local DataStore value is available.
5. **Bundled glyphs target the API 23 system emoji baseline.** Category and
   catalog symbols avoid newer glyphs that render as missing-character boxes
   on supported older devices. Richer bundled EmojiCompat typography can be
   reconsidered after the MVP.
6. **Success means observed persistence.** Setup and add confirmations are
   emitted only after the repository snapshot reflects the requested state.
   New feedback replaces an older banner so the user's latest action never
   waits behind stale celebration.
7. **The root owns dark-surface content color.** Compose backgrounds do not
   implicitly set `LocalContentColor`; Faveit explicitly supplies
   `onBackground` once at the root to keep headings legible across screens.

## Architecture and platform

1. **Single app module and one observable repository.** This is the smallest
   architecture that keeps data/UI boundaries testable without building a
   framework around a prototype.
2. **Preferences DataStore instead of Room.** The master catalog is immutable
   JSON and user state is a few sets/flags; a relational database adds no MVP
   value. The repository boundary allows a future Room or remote catalog source
   without changing screens.
3. **Stable catalog IDs are the authority.** User state stores IDs plus compact,
   URL-encoded overrides. Malformed override records are ignored instead of
   crashing startup.
4. **WorkManager handles reminders.** Exact timing is intentionally not
   promised; reliable, battery-respectful periodic work is the correct behavior
   for a non-urgent memory nudge.
5. **API 23 minimum, API 36 target.** This follows the current AndroidX default
   minimum while retaining broad device coverage. The build uses AGP 8.13.2,
   Kotlin 2.3.0, Compose BOM 2026.06.00, and Gradle 8.14.5. Core/lifecycle are
   deliberately pinned to their API-36-compatible stable versions; their next
   lines require API 37 and AGP 9.1+.
6. **No dependency injection or navigation framework.** A single application
   container and explicit three-destination state are easier to read and more
   than sufficient for this vertical slice.
7. **A corrupt preferences file resets to safe defaults.** With no server-side
   authority to repair local bytes, replacing an unreadable DataStore keeps the
   app launchable; the unavoidable loss of corrupt favorites is preferable to
   trapping every future launch on the loading screen.
