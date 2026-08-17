# Acceptance iteration 05 — automatic review resolution

Date: 2026-08-17
Branch: `agent/adaptive-catalog`
Pull request: [#4](https://github.com/mbeanii/Faveit/pull/4)

## Kind acceptance advocate

The automatic reviewer examined the exact initial PR head and found four
subtle issues that the local acceptance loop had missed. Each report was
specific, reproducible, and improved the promise that an update never surprises
or erases the user's own taste.

## Impossible-to-please acceptance critic

All four threads are release blockers:

1. Fourteen old filler IDs vanished from the master asset, silently hiding a
   saved favorite after update.
2. A saved tile announced itself as a checkbox but opened the editor when
   activated.
3. One-tap removal deleted local customization despite presenting re-add as
   reversible.
4. Returning to a setup category discarded its expanded “More picks” batch.

No thread may be waived merely because the common path works.

## Seasoned software architect

Keep rejected filler out of the searchable 1,680-item master, but append exact
compatibility-only runtime records for all 14 IDs. Mark them non-discoverable
and filter that flag at the search, setup, discovery-engine, and category
boundaries. Existing saved favorites remain visible and reversible; new users
never see the filler.

Expose a saved tile body as a management button with an explicit Favorite state
description, while an unsaved tile remains an add toggle and the red X remains
the independent remove action. Make removal affect only favorite membership;
preserve overrides until the explicit Reset defaults action. Save setup
shown/removed lists in category-keyed immutable maps with an explicit saver.

## Senior software developer

Implemented the architecture and added regressions:

- exact hidden compatibility records for all 14 prior IDs;
- tests proving compatibility records cannot enter search or discovery;
- category-keyed setup state plus a Compose round-trip test;
- corrected favorite-state semantics and updated journey assertions;
- an end-to-end customization/remove/re-add assertion;
- override-preserving removal.

The post-review matrix passed in 1 minute 13 seconds. All 32 JVM tests passed;
the six Android tests compile; debug, instrumentation, lint, R8, resource
shrinking, and release packaging pass. Lint remains at zero errors and only the
three pre-existing dependency-version warnings. `git diff --check` passes.

## Critic verdict

The four automatic-review findings are fully addressed and covered. Request a
fresh review on the new commit; merge only if the automatic reviewer reports no
remaining major issue on that exact SHA.
