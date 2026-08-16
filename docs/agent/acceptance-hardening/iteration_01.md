# Acceptance iteration 01

Branch: `agent/acceptance-hardening`

Status: implementation complete; proceed to critic iteration 2

## Kind acceptance advocate

This MVP has a great deal worth celebrating.

1. **The product thesis is already real.** Opening Home, searching
   `In N Out`, seeing `In-N-Out` identified as a Restaurant, and adding it
   without a form is a faithful vertical slice of the defining scenario.
2. **Recall is treated as the primary job.** Restaurants and the other seven
   categories are visually prominent, favorites are shown before discovery,
   and the two-column gem grid is much easier to scan than a settings-style
   list.
3. **First run is unusually focused.** Eight categories, twelve bundled entries
   apiece, instant tile toggles, progress, per-page counts, and a pinned action
   area form a fast setup experience without account or network friction.
4. **Favorite ownership is respected.** A user can rename, move, recolor,
   restore, and remove a favorite. Discovery is muted and separated. Nothing is
   sponsored, affiliate-ranked, or pretending to know the user better than
   their explicit choices.
5. **The gemstone identity is genuine product work.** Gradients, highlights,
   facets, depth, press animation, haptics, and click sound create a distinct
   tactile system without licensed commercial artwork or image-loading cost.
6. **Local-first is implemented, not advertised.** The app has no INTERNET
   permission, backend, login, analytics, advertising, or tracking dependency.
   Stable catalog IDs and compact local overrides give the prototype a credible
   replacement path for a larger catalog.
7. **Notifications honor the brief.** They are opt-in, weekly, local, restrained,
   permission-aware, channel-aware, and use the exact `Remember X?` form.
   Startup reconciliation, backup restoration, denial recovery, and corrupted
   preference recovery have already received unusually careful handling.
8. **The code is intentionally small.** One app module, one repository,
   DataStore, explicit destinations, and WorkManager are proportionate to an
   MVP. There is no speculative platform hidden underneath the prototype.
9. **Earlier review work materially improved quality.** Unicode and emoji
   search, legacy launcher support, system bars, notification entry, large-font
   layouts, editor scrolling, Back behavior, and asynchronous test waits are
   all handled.
10. **The handoff is honest.** START_HERE distinguishes compiled instrumentation
    coverage from a device-executed receipt and does not invent screenshots or
    performance evidence.

## Impossible-to-please acceptance critic

The product is promising, but acceptance is not yet complete.

### C1 — Setup selection is visually accessible but not semantically a toggle

`GemTile` always exposes a Button click role. During setup, selected state is
communicated primarily by color and a check icon. TalkBack users need an actual
checked/unchecked toggle state, not a descendant icon that may or may not be
announced coherently.

### C2 — Rapid add lacks the tactile contract used by the rest of Faveit

Gem tiles explicitly click and haptically confirm, while the core rapid-add row
uses ordinary Material click behavior. The most important write interaction is
therefore less characterful than category browsing. Its favorite-state change
also needs a clear accessibility state description/live update.

### C3 — The reminder control does not expose its current state

The icon changes and tint changes, but its content description remains only
`Favorite reminders`. A non-visual user cannot tell from the Home control
whether effective reminder delivery is on or off.

### C4 — Pure behavior coverage stops short of the management model

Codec tests prove serialization, but no host test proves that favorite IDs and
overrides resolve to the display name, category, palette, and favorite status
used by every screen. The exact notification-title contract is similarly
embedded inside the worker and untested.

### C5 — The compiled device journey covers only setup skip, add, and recall

It does not exercise rename, category reassignment, gem reassignment, restoring
defaults, removal, or rediscovery. Those are explicit MVP acceptance journeys,
not optional internal details.

### C6 — Speed, TalkBack traversal, haptic quality, and actual notification
delivery remain unproven on a device

The existing environment lacks usable hardware virtualization and the prior
software emulator never became ADB-ready. Compiling tests is valuable but does
not prove conversational speed or sensory quality. Acceptance records must keep
this boundary visible and provide executable device checks rather than lowering
the bar or fabricating evidence.

## Seasoned software architect

Each critic item maps to a bounded MVP improvement.

1. Add an explicit selection mode to `GemTile`. In selection mode use Compose
   toggle semantics with a Checkbox role and checked state; retain Button
   semantics for navigation and management tiles.
2. Give the rapid-add row one shared add action that performs Faveit's click
   sound and confirmation haptic from either the row or trailing button. Add a
   state description and polite live-region semantics so the transition to
   favorite is perceivable.
3. Make the reminder control description include its effective on/off state.
4. Extract catalog/preference projection into a pure repository function and
   notification title construction into a pure worker helper. Add focused JVM
   tests for default and customized favorites, moved categories/palettes,
   ignored unknown IDs, and exact reminder text.
5. Add stable editor tags and a second Compose instrumentation journey covering
   customize → move → recollect → reset → recollect → remove → rediscover.
6. Rebuild unit, debug, instrumentation, lint, and release artifacts. Attempt
   connected execution if a device becomes available; otherwise retain the
   explicit device gate in START_HERE and the final critic decision.

No account, backend, photo pipeline, AI integration, catalog service, or other
post-MVP platform work is justified by these findings.

## Senior software developer

Implemented every architecture item:

- `GemTile` now has a setup-only selection mode backed by Checkbox toggle
  semantics while navigation and management gems retain Button semantics.
- Search-result rows and their add buttons share confirmation haptics, click
  sound, favorite state descriptions, and a polite live-region update.
- The Home reminder control announces its effective on/off state.
- Repository display projection and reminder-title construction are pure,
  directly tested functions. Five new JVM tests cover catalog defaults, all
  local override dimensions, stale IDs, exact MVP reminder copy, and Unicode
  customized names.
- Stable category, gem, reset, and confirmation tags support a second compiled
  Compose journey through rename, category/gem reassignment, reset, removal,
  and rediscovery.

Validation after implementation:

- 21 JVM tests passed with zero failures.
- Debug APK assembly and Android lint passed.
- The expanded instrumentation APK compiled.
- The minified and resource-shrunk release APK passed R8, Compose mapping,
  release lint, and packaging.
- `git diff --check` passed.

Device execution remained an explicit gate at this iteration; it was pursued
again in iteration 2 rather than misreported here.
