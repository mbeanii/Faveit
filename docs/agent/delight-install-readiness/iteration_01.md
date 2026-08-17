# Acceptance iteration 01

Branch: `agent/delight-install-readiness`

Status: delight/readiness implementation complete; device evidence pending

## Kind acceptance advocate

The inherited MVP already delivers the complete product spine: eight-category
setup, a 96-item local catalog, forgiving `In N Out` search, one-tap add,
favorites-first recall, local customization/removal/reset, and opt-in local
reminders. It is unusually coherent for a prototype: the user's preferences
remain authoritative, every major category is visible on Home, no network or
account is required, and the gem grid is fast, memorable, and unmistakably its
own product.

The strongest victories are the conversationally direct information
architecture and the restraint. There is no feed, form, rating ceremony,
sponsored surface, or speculative platform. Existing JVM tests, compiled
Compose journeys, lint, debug packaging, and release shrinking provide a solid
engineering base.

## Impossible-to-please acceptance critic

That is not enough evidence to ask a busy user to install the app. The prior
receipt explicitly says no Android journey actually ran, no screenshot was
captured, and no physical-device behavior was observed. A compiled test is not
a user experience.

The user-trial invitation is blocked on these strict gates:

1. **G1 — honest Android execution:** cleanly install and launch an APK, then
   run the core journeys on an Android runtime; never relabel compilation as a
   device pass.
2. **G2 — immediate value:** one meaningful setup selection must be enough to
   enter the app. An eight-page forced tour is too expensive for first contact.
3. **G3 — trustworthy delight:** setup and rapid add need polished,
   accessible confirmation only after local persistence is observable. The
   message must name what was saved, and recreation must prove it stayed saved.
4. **G4 — smile-worthy first frame:** launch, setup, selection, and feedback
   must feel branded and tactile—not like framework defaults. Actual captures
   must be inspected for hierarchy, contrast, clipping, and chaotic color.
5. **G5 — privacy and artifact receipt:** clean builds, test counts, lint,
   release shrinking, artifact hashes/sizes, and the merged permission set must
   be recorded. In particular, no `INTERNET` permission or tracker may hide in
   the packaged APK.
6. **G6 — respectful handoff:** provide direct phone-install steps, identify
   the APK as debug-signed, state what uninstalling does to local data, and give
   the user a short first-session script worth their time.

## Seasoned software architect

1. Introduce a small code-rendered emerald `GemMark` shared by launch/loading,
   setup guidance, and success feedback; do not add licensed or decoded art.
2. Make setup escapable after the first selected favorite with a clearly
   labeled `Start now` action while retaining full-category browsing.
3. Model success feedback as pending intent reconciled against the repository
   snapshot. Show a custom emerald banner only when `isFavorite` or
   `setupComplete` is observed; give the banner a polite live-region semantic.
4. Give selected gem tiles persistent scale, glint, and elevation while keeping
   the existing press depression, haptic, and click sound.
5. Replace the plain launch window/loading label with a branded local launch
   drawable and in-Compose mark; report fully drawn only after state loads.
6. Strengthen the journey test to use the early exit, assert both persisted
   confirmations, recreate the Activity, and then exercise recall. Add focused
   copy and accessibility tests.

## Senior software developer

Implemented the architecture:

- Added `GemMark`, `DelightBanner`, branded launch background, and branded
  loading state.
- Added one-favorite `Start now` setup completion with confirmation haptics.
- Added snapshot-confirmed setup/add banners and exact copy helpers.
- Enhanced selected/pressed gem depth and glint.
- Added `reportFullyDrawn()` after the local snapshot loads.
- Expanded the Android suite from four to five tests and the JVM suite from 21
  to 23 tests.

The local clean matrix had not yet been converted into Android runtime evidence,
so the critic remained unsatisfied and required another iteration.
