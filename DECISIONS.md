# Decisions

Consequential MVP decisions and ambiguous-spec interpretations are recorded
here so a future maintainer can change them deliberately.

## Product

1. **Suggestions are local, transparent, and subordinate to explicit taste.**
   Setup begins with popular choices from distinct facets. After a user chooses
   favorites, each requested batch targets six content-nearest neighbors and six
   exploratory choices. Similarity is Jaccard overlap on curated tags, not a
   trained profile, demographic inference, remote model, or engagement score.
   Stable band shuffling provides variety without making tests or UI order
   unpredictable. Explicit favorites always remain the authority.
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
7. **Discovery is progressive, not an endless initial scroll.** Each category
   starts with 12 recognizable, facet-diverse choices. `More picks` appends 12
   without reordering anything already shown; this keeps selection position and
   muscle memory stable.
8. **Removal never makes the target flee.** Search toggles plus/check in one tap.
   Setup and category grids use an independent red corner X; removal greys the
   tile in place so a mistaken tap can be reversed. The body of a saved tile
   remains the route to customization.

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
8. **Search preprocessing happens once.** The repository owns an immutable
   `CatalogSearchIndex` that normalizes names and aliases at load time. A query
   normalizes only itself and the tiny set of user-renamed favorites, avoiding a
   full-catalog reconstruction on every keystroke.
9. **The catalog is generated, researched, and migration-conscious.** The
   canonical Python source contains 21 ten-item facets per category and emits
   deterministic JSON. Popularity is a transparent category-local curation
   order, not a claim of precise universal ranking. Stable IDs preserve all 82
   concrete choices from the shipped MVP; 14 invented venue or vague playlist
   placeholders are omitted from discovery but retained as hidden compatibility
   records so an existing saved favorite never disappears.

10. **Existing concrete favorites retain their original visual defaults.** The
    generator carries the shipped emoji and gem palette for all 82 retained
    IDs. A catalog expansion can improve metadata and ordering, but updating
    the app must not unexpectedly restyle a user's established favorites.
11. **Full-catalog projection runs away from the main thread.** DataStore
    emissions resolve the 1,680 display items on `Dispatchers.Default`; Compose
    receives completed snapshots. This keeps catalog growth from turning a
    preference edit into UI-thread list reconstruction.
12. **Faveit initializes WorkManager explicitly.** The library Startup metadata
    is removed and `FaveitApplication` initializes WorkManager synchronously
    before reminder reconciliation. This removes an observed process-restart
    race while keeping reminders entirely local and battery-respectful.

13. **Reversible removal preserves customization.** Removing a favorite changes
    membership only. Local name, category, and gem overrides survive so a
    one-tap re-add restores the same tile; Reset defaults remains the deliberate
    action that discards those choices.
14. **Tile roles follow their actions.** An unsaved tile is an add toggle. A
    saved tile body is a management button with an explicit Favorite state, and
    its red X is the independent removal action.
15. **Setup browsing state is category-keyed.** Expanded and removed-item lists
    are stored in immutable saveable maps, so moving between categories or
    recreating the Activity does not discard browsing progress.
16. **Recall aliases are stable migration data.** Every synonym shipped for a
    retained catalog ID remains searchable even when the item's canonical name,
    facet, or new aliases change. The generator merges old and new vocabulary
    by ID and rejects any old alias mapping whose ID is no longer generated.
17. **Retired-item recovery is scoped to its previous owner.** DataStore keeps a
    compact set of IDs the user genuinely favorited. That set admits otherwise
    hidden compatibility records to search and Discover More after removal,
    while a fresh user never sees the rejected filler. Current favorites
    implicitly migrate, so upgrading does not require a one-time blocking job.
18. **Pinned removals are both saveable and durable.** Category pinned IDs use
    Compose saveable state so rotation/process recreation keeps a removed tile
    in place. The remembered-favorite set is the durable fallback across a cold
    session: the item remains searchable and discoverable in its effective
    locally customized category.
