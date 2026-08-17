# Acceptance iteration 02 — adaptive discovery and reversible controls

Date: 2026-08-17
Branch: `agent/adaptive-catalog`

## Kind acceptance advocate

Faveit now turns the larger catalog into a focused setup experience. Each page
begins with only 12 popular, facet-diverse choices. “More picks” appends rather
than reshuffles. Once favorites exist, subsequent batches visibly mix related
choices with exploration. The user remains the sole taste authority; there is
no remote model, demographic inference, tracking, or engagement score.

The requested removal paths are crisp. Search's saved checkmark is a one-tap
unfavorite control. Setup and category tiles have a separate 44dp red X.
Removal leaves a grey tile exactly where it was, ready for one-tap reversal,
while the body of a saved tile remains the customization path.

## Impossible-to-please acceptance critic

The feature is not accepted merely because it is called “ML-like.” It must
prove:

1. Initial picks are both popular and facet-diverse.
2. With sufficient candidates, an adaptive 12-item batch is exactly six
   content-neighbors and six exploratory choices.
3. Sharing only the broad category tag cannot create a false neighbor.
4. Results are deterministic and already-visible tiles never reorder.
5. Related results cannot be monopolized by one facet.
6. Search does not re-normalize all 1,680 items on every keystroke.
7. Local custom names and aliases remain searchable.
8. Removing from all three surfaces is truly one tap, accessible, reversible,
   and independent from the edit action.
9. All 82 retained entries preserve their original emoji and gem palette.

## Seasoned software architect

Implement a transparent content-based nearest-neighbor engine using Jaccard
over curated tags, excluding the category membership tag. Select related
candidates by maximum similarity to any explicit favorite, cap facet
concentration, and interleave them with stable exploration from popularity
bands. Persist only explicit user choices; derive suggestions locally.

Build one immutable normalized search index at repository construction and
normalize only the query plus the tiny local custom-name map. Separate the
tile's body semantics from a corner removal control. Pin removed favorites
inside the current category section and preserve setup's shown-ID list. Add a
legacy visual-default table to the generator and fail generation if any
retained ID disappears.

## Senior software developer

Added `DiscoveryEngine`, indexed ranked search, progressive setup batches,
pinned category removal state, search add/remove toggles, independent red-X
controls with haptic/click feedback, and legacy visual mappings.

Automated tests prove initial facet diversity, deterministic ordering, exact
6/6 related/exploratory composition, category-tag exclusion, custom-name and
alias search, punctuation/diacritic/non-Latin behavior, and the 82-entry visual
migration contract. The 1,680-item reusable index completed a 250-query typing
workload in 0.283 seconds on the validation host.

## Critic verdict

The behavioral and migration gates pass. Continue. The next objection is scale
at the UI boundary: a fast search function is not enough if every DataStore
change rebuilds 1,680 display rows on the main thread, or if only isolated unit
tests support the interaction claims.
